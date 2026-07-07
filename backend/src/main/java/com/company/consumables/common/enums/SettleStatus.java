package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 应收/应付结清状态枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Getter
public enum SettleStatus {

    /** 未结清（未收清/未付清） */
    UNSETTLED(1, "未结清"),

    /** 已结清（已收清/已付清） */
    SETTLED(2, "已结清");

    /** 状态编码 */
    private final int code;

    /** 状态描述 */
    private final String desc;

    SettleStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
