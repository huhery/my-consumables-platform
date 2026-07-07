package com.company.consumables.common.enums;

import lombok.Getter;

/**
 * 类描述: 资金流水方向枚举
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Getter
public enum FundDirection {

    /** 收入 */
    INCOME(1, "收入"),

    /** 支出 */
    EXPENSE(2, "支出");

    /** 方向编码 */
    private final int code;

    /** 方向描述 */
    private final String desc;

    FundDirection(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
