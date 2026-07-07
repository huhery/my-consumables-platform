package com.company.consumables.common.util;

import com.company.consumables.common.constant.Constant;

/**
 * 类描述: 取值工具，统一处理可选字段的 null 规整，保证落库字段非 NULL
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public final class ValueUtil {

    private ValueUtil() {
    }

    /**
     * 功能描述: 字符串为 null 时返回空字符串，否则原样返回
     *
     * @param value 原值
     * @return 非 null 字符串
     * @author honghui
     * @date 2026/06/30 15:30
     */
    public static String defaultEmpty(String value) {
        return value == null ? Constant.EMPTY : value;
    }
}
