package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 租户/账号状态枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Getter
public enum TenantStatus {

    /** 启用 */
    ENABLED(1, "启用"),

    /** 停用 */
    DISABLED(2, "停用");

    /** 状态编码 */
    private final int code;

    /** 状态描述 */
    private final String desc;

    TenantStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
