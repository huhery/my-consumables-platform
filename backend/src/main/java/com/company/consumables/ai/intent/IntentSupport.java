package com.company.consumables.ai.intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 类描述: 意图处理公共工具。提供日期范围解析（缺省本月）、金额格式化（分转元）等，
 *         供各 IntentHandler 复用，避免重复代码。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
public final class IntentSupport {

    private IntentSupport() {
    }

    /** 日期格式 yyyy-MM-dd */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 功能描述: 从参数解析起始日期，无则返回本月第一天
     *
     * @param params 参数
     * @return 起始日期
     * @author honghui
     * @date 2026/07/08 11:30
     */
    public static Date startDateOrMonthStart(Map<String, Object> params) {
        Date d = parseDate(params, "startDate");
        if (d != null) {
            return d;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return truncate(cal);
    }

    /**
     * 功能描述: 从参数解析结束日期，无则返回今天
     *
     * @param params 参数
     * @return 结束日期
     * @author honghui
     * @date 2026/07/08 11:30
     */
    public static Date endDateOrToday(Map<String, Object> params) {
        Date d = parseDate(params, "endDate");
        if (d != null) {
            return d;
        }
        return truncate(Calendar.getInstance());
    }

    /**
     * 功能描述: 格式化日期为 yyyy-MM-dd 文本
     *
     * @param date 日期
     * @return 文本
     * @author honghui
     * @date 2026/07/08 11:30
     */
    public static String formatDate(Date date) {
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }

    /**
     * 功能描述: 分转元文本（两位小数，带千分位）
     *
     * @param cents 金额（分），可为 null（按 0 处理）
     * @return 元文本
     * @author honghui
     * @date 2026/07/08 11:30
     */
    public static String yuan(Number cents) {
        long c = cents == null ? 0L : cents.longValue();
        return String.format("%,.2f", c / 100.0);
    }

    /**
     * 功能描述: 解析参数中的日期字段（yyyy-MM-dd）
     *
     * @param params 参数
     * @param key    字段名
     * @return 日期，缺失或非法返回 null
     * @author honghui
     * @date 2026/07/08 11:30
     */
    private static Date parseDate(Map<String, Object> params, String key) {
        if (params == null) {
            return null;
        }
        Object v = params.get(key);
        if (v == null) {
            return null;
        }
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            sdf.setLenient(false);
            return sdf.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 功能描述: 取整到当天零点
     *
     * @param cal 日历
     * @return 当天零点日期
     * @author honghui
     * @date 2026/07/08 11:30
     */
    private static Date truncate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 功能描述: 从参数取整数（如 topN），缺失或非法返回默认值
     *
     * @param params       参数
     * @param key          字段名
     * @param defaultValue 默认值
     * @return 整数值
     * @author honghui
     * @date 2026/07/08 11:30
     */
    public static int intOrDefault(Map<String, Object> params, String key, int defaultValue) {
        if (params == null || params.get(key) == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(params.get(key)).trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
