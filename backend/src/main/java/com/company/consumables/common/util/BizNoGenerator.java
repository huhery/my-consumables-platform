package com.company.consumables.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 类描述: 业务单号生成工具，格式为 前缀 + yyyyMMddHHmmss + 4位随机数
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public final class BizNoGenerator {

    private BizNoGenerator() {
    }

    /** 日期时间格式 */
    private static final String TIME_PATTERN = "yyyyMMddHHmmss";

    /** 随机数下界 */
    private static final int RANDOM_LOWER = 1000;

    /** 随机数上界 */
    private static final int RANDOM_UPPER = 9999;

    /**
     * 功能描述: 生成业务单号
     *
     * @param prefix 单号前缀（如 PO、SO）
     * @return 业务单号
     * @author honghui
     * @date 2026/06/30 15:02
     */
    public static String generate(String prefix) {
        String time = new SimpleDateFormat(TIME_PATTERN).format(new Date());
        int random = ThreadLocalRandom.current().nextInt(RANDOM_LOWER, RANDOM_UPPER + 1);
        return prefix + time + random;
    }
}
