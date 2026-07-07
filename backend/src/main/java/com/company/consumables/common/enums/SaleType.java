package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 销售类型枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Getter
public enum SaleType {

    /** 批发出货（发超市） */
    WHOLESALE(1, "批发出货"),

    /** 门店散卖 */
    STORE(2, "门店散卖");

    /** 类型编码 */
    private final int code;

    /** 类型描述 */
    private final String desc;

    SaleType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
