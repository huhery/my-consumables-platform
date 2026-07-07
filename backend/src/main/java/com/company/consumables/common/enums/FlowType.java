package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 出入库流水类型枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Getter
public enum FlowType {

    /** 采购入库（数量为正） */
    PURCHASE_IN(1, "采购入库"),

    /** 批发出库（数量为负） */
    WHOLESALE_OUT(2, "批发出库"),

    /** 门店散卖出库（数量为负） */
    STORE_OUT(3, "门店散卖出库"),

    /** 手工调整（数量可正可负） */
    MANUAL_ADJUST(4, "手工调整");

    /** 类型编码 */
    private final int code;

    /** 类型描述 */
    private final String desc;

    FlowType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
