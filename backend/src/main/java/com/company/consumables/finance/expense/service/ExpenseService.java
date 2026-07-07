package com.company.consumables.finance.expense.service;

import com.company.consumables.finance.expense.entity.ExpenseCategory;
import com.company.consumables.finance.expense.vo.ExpenseCategorySaveVo;
import com.company.consumables.finance.expense.vo.ExpenseSaveVo;

import java.util.List;

/**
 * 类描述: 费用/收入分类管理与记账服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface ExpenseService {

    /**
     * 功能描述: 新增分类
     *
     * @param vo 分类入参
     * @return 主键
     * @author honghui
     * @date 2026/07/06 11:40
     */
    String createCategory(ExpenseCategorySaveVo vo);

    /**
     * 功能描述: 删除分类（已被资金流水引用时拒绝）
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/07/06 11:40
     */
    void deleteCategory(String sId);

    /**
     * 功能描述: 按类型查询分类（type 为 null 查全部）
     *
     * @param type 分类类型
     * @return 分类列表
     * @author honghui
     * @date 2026/07/06 11:40
     */
    List<ExpenseCategory> listCategory(Integer type);

    /**
     * 功能描述: 登记费用支出，生成费用支出资金流水
     *
     * @param vo 记账入参
     * @author honghui
     * @date 2026/07/06 11:40
     */
    void recordExpense(ExpenseSaveVo vo);

    /**
     * 功能描述: 登记其他收入，生成其他收入资金流水
     *
     * @param vo 记账入参
     * @author honghui
     * @date 2026/07/06 11:40
     */
    void recordIncome(ExpenseSaveVo vo);
}
