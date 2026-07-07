package com.company.consumables.finance.expense.service.impl;

import com.company.consumables.common.enums.FundDirection;
import com.company.consumables.common.enums.FundFlowType;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.finance.expense.entity.ExpenseCategory;
import com.company.consumables.finance.expense.mapper.ExpenseCategoryMapper;
import com.company.consumables.finance.expense.service.ExpenseService;
import com.company.consumables.finance.expense.vo.ExpenseCategorySaveVo;
import com.company.consumables.finance.expense.vo.ExpenseSaveVo;
import com.company.consumables.finance.fund.mapper.FundFlowMapper;
import com.company.consumables.finance.fund.service.FundFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述: 费用/收入分类管理与记账服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseCategoryMapper categoryMapper;
    private final FundFlowMapper fundFlowMapper;
    private final FundFlowService fundFlowService;

    /**
     * 功能描述: 新增分类，校验同类型下名称不重复
     *
     * @param vo 分类入参
     * @return 主键
     * @author honghui
     * @date 2026/07/06 11:45
     */
    @Override
    public String createCategory(ExpenseCategorySaveVo vo) {
        if (categoryMapper.countByNameAndType(vo.getSName(), vo.getICategoryType(), null) > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATE);
        }
        ExpenseCategory category = new ExpenseCategory();
        category.setSName(vo.getSName());
        category.setICategoryType(vo.getICategoryType());
        categoryMapper.insert(category);
        return category.getSId();
    }

    /**
     * 功能描述: 删除分类，已被资金流水引用时拒绝
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/07/06 11:45
     */
    @Override
    public void deleteCategory(String sId) {
        ExpenseCategory category = categoryMapper.selectById(sId);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        if (fundFlowMapper.countByCategoryId(sId) > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_IN_USE);
        }
        categoryMapper.deleteById(sId);
    }

    /**
     * 功能描述: 按类型查询分类
     *
     * @param type 分类类型
     * @return 分类列表
     * @author honghui
     * @date 2026/07/06 11:45
     */
    @Override
    public List<ExpenseCategory> listCategory(Integer type) {
        return categoryMapper.selectByType(type);
    }

    /**
     * 功能描述: 登记费用支出并生成资金流水
     *
     * @param vo 记账入参
     * @author honghui
     * @date 2026/07/06 11:45
     */
    @Override
    public void recordExpense(ExpenseSaveVo vo) {
        validateCategory(vo.getSCategoryId());
        fundFlowService.record(FundFlowType.EXPENSE, FundDirection.EXPENSE, vo.getIAmount(),
                null, null, vo.getSCategoryId(), vo.getDOccurDate(), vo.getSRemark());
    }

    /**
     * 功能描述: 登记其他收入并生成资金流水
     *
     * @param vo 记账入参
     * @author honghui
     * @date 2026/07/06 11:45
     */
    @Override
    public void recordIncome(ExpenseSaveVo vo) {
        validateCategory(vo.getSCategoryId());
        fundFlowService.record(FundFlowType.OTHER_INCOME, FundDirection.INCOME, vo.getIAmount(),
                null, null, vo.getSCategoryId(), vo.getDOccurDate(), vo.getSRemark());
    }

    /**
     * 功能描述: 校验分类存在
     *
     * @param categoryId 分类ID
     * @author honghui
     * @date 2026/07/06 11:45
     */
    private void validateCategory(String categoryId) {
        if (categoryMapper.selectById(categoryId) == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }
}
