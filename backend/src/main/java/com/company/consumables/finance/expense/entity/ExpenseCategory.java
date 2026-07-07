package com.company.consumables.finance.expense.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 费用/收入分类实体，对应表 TAB_EXPENSE_CATEGORY
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpenseCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 分类名称 */
    private String sName;

    /** 分类类型：1支出 2收入 */
    private Integer iCategoryType;
}
