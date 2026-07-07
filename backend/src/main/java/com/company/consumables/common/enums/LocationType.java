package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 库存地点类型枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Getter
public enum LocationType {

    /** 仓库 */
    WAREHOUSE(1, "仓库"),

    /** 门店（可散卖） */
    STORE(2, "门店");

    /** 类型编码 */
    private final int code;

    /** 类型描述 */
    private final String desc;

    LocationType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
