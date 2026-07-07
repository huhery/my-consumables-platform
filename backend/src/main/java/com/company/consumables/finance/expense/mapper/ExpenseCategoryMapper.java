package com.company.consumables.finance.expense.mapper;

import com.company.consumables.finance.expense.entity.ExpenseCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 费用/收入分类 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface ExpenseCategoryMapper {

    /**
     * 功能描述: 新增分类
     *
     * @param category 分类实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 11:30
     */
    int insert(ExpenseCategory category);

    /**
     * 功能描述: 更新分类
     *
     * @param category 分类实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 11:30
     */
    int update(ExpenseCategory category);

    /**
     * 功能描述: 按主键物理删除
     *
     * @param sId 主键
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 11:30
     */
    int deleteById(@Param("sId") String sId);

    /**
     * 功能描述: 按主键查询
     *
     * @param sId 主键
     * @return 分类实体，可能为 null
     * @author honghui
     * @date 2026/07/06 11:30
     */
    ExpenseCategory selectById(@Param("sId") String sId);

    /**
     * 功能描述: 校验同类型下名称是否重复（排除指定主键）
     *
     * @param name      分类名称
     * @param type      分类类型
     * @param excludeId 需排除的主键
     * @return 数量
     * @author honghui
     * @date 2026/07/06 11:30
     */
    int countByNameAndType(@Param("name") String name,
                           @Param("type") int type,
                           @Param("excludeId") String excludeId);

    /**
     * 功能描述: 按类型查询分类列表
     *
     * @param type 分类类型（null 查全部）
     * @return 分类列表
     * @author honghui
     * @date 2026/07/06 11:30
     */
    List<ExpenseCategory> selectByType(@Param("type") Integer type);
}
