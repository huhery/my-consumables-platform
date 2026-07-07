package com.company.consumables.common.constant;

/**
 * 类描述: 全局常量定义，禁止在业务代码中散落魔法值
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public final class Constant {

    private Constant() {
    }

    /** 空字符串默认值 */
    public static final String EMPTY = "";

    /** 默认分页页码 */
    public static final int DEFAULT_PAGE_NUM = 1;

    /** 默认分页每页条数 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** 基本单位换算率（基本单位自身换算率恒为 1） */
    public static final int BASE_UNIT_RATE = 1;

    /** 进货单号前缀 */
    public static final String PURCHASE_NO_PREFIX = "PO";

    /** 销售单号前缀 */
    public static final String SALE_NO_PREFIX = "SO";

    /** 审计字段：创建时间属性名 */
    public static final String FIELD_CREATE_TIME = "dtCreateTime";

    /** 审计字段：更新时间属性名 */
    public static final String FIELD_UPDATE_TIME = "dtUpdateTime";

    /** 审计字段：创建人属性名 */
    public static final String FIELD_CREATE_USER = "sCreateUser";

    /** 审计字段：更新人属性名 */
    public static final String FIELD_UPDATE_USER = "sUpdateUser";

    /** 审计字段：主键属性名 */
    public static final String FIELD_ID = "sId";
}
