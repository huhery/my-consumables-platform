package com.company.consumables.common.tenant;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 类描述: 多租户 SQL 拦截器。对业务表的 SELECT/UPDATE/DELETE 自动追加 S_TENANT_ID 过滤条件，
 *         平台级表（租户表、账号表）豁免。基于 JSqlParser 改写 SQL，实现框架层自动隔离。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare",
                args = {Connection.class, Integer.class})
})
public class TenantSqlInterceptor implements Interceptor {

    /** 租户隔离字段名 */
    private static final String TENANT_COLUMN = "S_TENANT_ID";

    /** 平台级表白名单：不参与租户隔离 */
    private static final Set<String> IGNORE_TABLES = new HashSet<>(Arrays.asList(
            "TAB_TENANT", "TAB_ACCOUNT"
    ));

    /**
     * 功能描述: 拦截 SQL 预编译，改写业务表 SQL 追加租户条件
     *
     * @param invocation 拦截调用
     * @return 原方法执行结果
     * @throws Throwable 执行异常
     * @author honghui
     * @date 2026/07/12 11:00
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String tenantId = TenantContext.getTenantId();
        // 无租户上下文（如平台管理员操作平台表、系统初始化）时不改写
        if (!StringUtils.hasText(tenantId)) {
            return invocation.proceed();
        }

        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        MappedStatement mappedStatement =
                (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        SqlCommandType commandType = mappedStatement.getSqlCommandType();

        // 仅处理增删改查中的查询/更新/删除；INSERT 由审计拦截器填充租户列
        if (commandType == SqlCommandType.INSERT || commandType == SqlCommandType.UNKNOWN) {
            return invocation.proceed();
        }

        BoundSql boundSql = handler.getBoundSql();
        String originalSql = boundSql.getSql();
        String newSql = addTenantCondition(originalSql, tenantId);
        if (newSql != null && !newSql.equals(originalSql)) {
            metaObject.setValue("delegate.boundSql.sql", newSql);
        }
        return invocation.proceed();
    }

    /**
     * 功能描述: 使用 JSqlParser 为业务表 SQL 追加租户过滤条件
     *
     * @param sql      原始 SQL
     * @param tenantId 当前租户ID
     * @return 改写后的 SQL，无需改写或解析失败时返回原 SQL
     * @author honghui
     * @date 2026/07/12 11:00
     */
    private String addTenantCondition(String sql, String tenantId) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);

            if (statement instanceof Select) {
                SelectBody selectBody = ((Select) statement).getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    if (isBusinessTable(plainSelect.getFromItem())) {
                        Table mainTable = (Table) plainSelect.getFromItem();
                        Expression tenantExpr = buildTenantEquals(tenantId, mainTable);
                        plainSelect.setWhere(mergeWhere(plainSelect.getWhere(), tenantExpr));
                        return statement.toString();
                    }
                }
            } else if (statement instanceof Update) {
                Update update = (Update) statement;
                if (isBusinessTable(update.getTable())) {
                    Expression tenantExpr = buildTenantEquals(tenantId, update.getTable());
                    update.setWhere(mergeWhere(update.getWhere(), tenantExpr));
                    return statement.toString();
                }
            } else if (statement instanceof Delete) {
                Delete delete = (Delete) statement;
                if (isBusinessTable(delete.getTable())) {
                    Expression tenantExpr = buildTenantEquals(tenantId, delete.getTable());
                    delete.setWhere(mergeWhere(delete.getWhere(), tenantExpr));
                    return statement.toString();
                }
            }
        } catch (Exception e) {
            // 解析失败时保守放行原 SQL，但记录警告以便排查
            log.warn("租户 SQL 改写失败，放行原 SQL：{}", sql, e);
        }
        return sql;
    }

    /**
     * 功能描述: 判断表是否为需隔离的业务表（非白名单）
     *
     * @param fromItem from 项
     * @return true 表示业务表
     * @author honghui
     * @date 2026/07/12 11:00
     */
    private boolean isBusinessTable(Object fromItem) {
        if (!(fromItem instanceof Table)) {
            return false;
        }
        String tableName = ((Table) fromItem).getName().replace("`", "").toUpperCase();
        return !IGNORE_TABLES.contains(tableName);
    }

    /**
     * 功能描述: 构造 S_TENANT_ID = '租户ID' 表达式，带主表别名（解决 JOIN 时列名歧义）
     *
     * @param tenantId 租户ID
     * @param table    主表（用于获取别名）
     * @return 等值表达式
     * @author honghui
     * @date 2026/07/12 11:00
     */
    private Expression buildTenantEquals(String tenantId, Table table) {
        EqualsTo equalsTo = new EqualsTo();
        // 优先用表别名，无别名用表名，确保 JOIN 时不歧义
        String prefix = table.getAlias() != null ? table.getAlias().getName() : table.getName();
        equalsTo.setLeftExpression(new Column(new Table(prefix), TENANT_COLUMN));
        equalsTo.setRightExpression(new StringValue(tenantId));
        return equalsTo;
    }

    /**
     * 功能描述: 将租户条件与原 WHERE 用 AND 合并
     *
     * @param original 原 WHERE
     * @param tenant   租户条件
     * @return 合并后的 WHERE
     * @author honghui
     * @date 2026/07/12 11:00
     */
    private Expression mergeWhere(Expression original, Expression tenant) {
        if (original == null) {
            return tenant;
        }
        return new net.sf.jsqlparser.expression.operators.conditional.AndExpression(original, tenant);
    }

    /**
     * 功能描述: 包装目标对象为代理
     *
     * @param target 目标对象
     * @return 代理对象
     * @author honghui
     * @date 2026/07/12 11:00
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(java.util.Properties properties) {
        // 无需额外属性
    }
}
