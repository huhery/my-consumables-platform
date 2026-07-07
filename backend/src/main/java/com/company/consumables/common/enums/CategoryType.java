package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 费用/收入分类类型枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Getter
public enum CategoryType {

    /** 支出 */
    EXPENSE(1, "支出"),

    /** 收入 */
    INCOME(2, "收入");

    /** 类型编码 */
    private final int code;

    /** 类型描述 */
    private final String desc;

    CategoryType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
