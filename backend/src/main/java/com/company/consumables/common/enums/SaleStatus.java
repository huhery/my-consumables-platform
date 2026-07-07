package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 销售单据状态枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Getter
public enum SaleStatus {

    /** 待发货 */
    PENDING(1, "待发货"),

    /** 已完成 */
    COMPLETED(2, "已完成");

    /** 状态编码 */
    private final int code;

    /** 状态描述 */
    private final String desc;

    SaleStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
