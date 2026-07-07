package com.company.consumables.common.audit;

import com.company.consumables.common.context.UserContext;
import com.company.consumables.common.entity.BaseEntity;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * 类描述: 审计字段自动填充拦截器。INSERT 填充主键与全部审计字段，UPDATE 填充更新时间与更新人
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update",
                args = {MappedStatement.class, Object.class})
})
public class AuditFieldInterceptor implements Interceptor {

    /** 第一期免登录场景的默认操作人 */
    @Value("${consumables.audit.default-user:admin}")
    private String defaultUser;

    /**
     * 功能描述: 拦截 update 操作，按命令类型填充审计字段
     *
     * @param invocation 拦截调用
     * @return 原方法执行结果
     * @throws Throwable 执行异常
     * @author honghui
     * @date 2026/06/30 10:55
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        SqlCommandType commandType = mappedStatement.getSqlCommandType();

        Date now = new Date();
        String operator = resolveOperator();

        String tenantId = com.company.consumables.common.tenant.TenantContext.getTenantId();

        if (SqlCommandType.INSERT == commandType) {
            fillEntities(parameter, entity -> fillInsert(entity, now, operator, tenantId));
        } else if (SqlCommandType.UPDATE == commandType) {
            fillEntities(parameter, entity -> fillUpdate(entity, now, operator));
        }

        return invocation.proceed();
    }

    /**
     * 功能描述: 解析当前操作人，无登录上下文时取默认用户
     *
     * @return 操作人标识
     * @author honghui
     * @date 2026/06/30 10:55
     */
    private String resolveOperator() {
        String currentUser = UserContext.getCurrentUser();
        return StringUtils.hasText(currentUser) ? currentUser : defaultUser;
    }

    /**
     * 功能描述: 从参数中提取 BaseEntity 实例并执行填充动作，兼容单实体、Map 包装、集合批量
     *
     * @param parameter MyBatis 方法参数
     * @param filler    填充动作
     * @author honghui
     * @date 2026/06/30 10:55
     */
    private void fillEntities(Object parameter, EntityFiller filler) {
        if (parameter == null) {
            return;
        }
        if (parameter instanceof BaseEntity) {
            filler.fill((BaseEntity) parameter);
        } else if (parameter instanceof Map) {
            for (Object value : ((Map<?, ?>) parameter).values()) {
                fillEntities(value, filler);
            }
        } else if (parameter instanceof Collection) {
            for (Object item : (Collection<?>) parameter) {
                fillEntities(item, filler);
            }
        }
    }

    /**
     * 功能描述: 填充新增场景的审计字段（主键、创建/更新时间、创建/更新人）
     *
     * @param entity   实体
     * @param now      当前时间
     * @param operator 操作人
     * @author honghui
     * @date 2026/06/30 10:55
     */
    private void fillInsert(BaseEntity entity, Date now, String operator, String tenantId) {
        if (!StringUtils.hasText(entity.getSId())) {
            entity.setSId(UUID.randomUUID().toString().replace("-", ""));
        }
        entity.setDtCreateTime(now);
        entity.setDtUpdateTime(now);
        entity.setSCreateUser(operator);
        entity.setSUpdateUser(operator);
        // 多租户：新增业务实体时自动填充租户ID（若实体未显式设置且存在租户上下文）
        if (StringUtils.hasText(tenantId) && !StringUtils.hasText(entity.getSTenantId())) {
            entity.setSTenantId(tenantId);
        }
    }

    /**
     * 功能描述: 填充更新场景的审计字段（更新时间、更新人）
     *
     * @param entity   实体
     * @param now      当前时间
     * @param operator 操作人
     * @author honghui
     * @date 2026/06/30 10:55
     */
    private void fillUpdate(BaseEntity entity, Date now, String operator) {
        entity.setDtUpdateTime(now);
        entity.setSUpdateUser(operator);
    }

    /**
     * 功能描述: 包装目标对象为代理，使拦截器生效
     *
     * @param target 目标对象
     * @return 代理对象
     * @author honghui
     * @date 2026/06/30 10:55
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 无需额外属性配置
    }

    /**
     * 功能描述: 实体填充动作的函数式接口
     *
     * @author honghui
     * @date 2026/06/30 10:55
     */
    @FunctionalInterface
    private interface EntityFiller {
        void fill(BaseEntity entity);
    }
}
