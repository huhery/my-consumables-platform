package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 资金流水类型枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Getter
public enum FundFlowType {

    /** 销售收款 */
    SALE_RECEIPT(1, "销售收款"),

    /** 采购付款 */
    PURCHASE_PAYMENT(2, "采购付款"),

    /** 费用支出 */
    EXPENSE(3, "费用支出"),

    /** 其他收入 */
    OTHER_INCOME(4, "其他收入");

    /** 类型编码 */
    private final int code;

    /** 类型描述 */
    private final String desc;

    FundFlowType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
