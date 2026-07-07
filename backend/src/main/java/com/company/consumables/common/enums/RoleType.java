package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 账号角色枚举（预留将来扩展业务员、仓管等）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Getter
public enum RoleType {

    /** 平台管理员 */
    PLATFORM_ADMIN(1, "平台管理员"),

    /** 商家管理员 */
    TENANT_ADMIN(2, "商家管理员");

    /** 角色编码 */
    private final int code;

    /** 角色描述 */
    private final String desc;

    RoleType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
