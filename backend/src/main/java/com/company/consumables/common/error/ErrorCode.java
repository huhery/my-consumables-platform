package com.company.consumables.common.error;

/**
 * 类描述: 业务错误码常量定义（码值字符串），对应中文消息见 error-code.properties
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public final class ErrorCode {

    private ErrorCode() {
    }

    /** 通用参数错误 */
    public static final String PARAM_INVALID = "PARAM_INVALID";

    /** 系统通用错误 */
    public static final String SYSTEM_ERROR = "SYSTEM_ERROR";

    // ===== 商品 =====
    /** 商品编码已存在 */
    public static final String GOODS_CODE_DUPLICATE = "GOODS_CODE_DUPLICATE";
    /** 商品已被使用，不允许删除 */
    public static final String GOODS_IN_USE = "GOODS_IN_USE";
    /** 商品已被使用，不允许修改基本单位 */
    public static final String GOODS_BASE_UNIT_LOCKED = "GOODS_BASE_UNIT_LOCKED";
    /** 商品不存在 */
    public static final String GOODS_NOT_FOUND = "GOODS_NOT_FOUND";

    // ===== 单位换算 =====
    /** 换算率必须大于 0 */
    public static final String UNIT_RATE_INVALID = "UNIT_RATE_INVALID";
    /** 已有流水，不允许修改换算率 */
    public static final String UNIT_RATE_LOCKED = "UNIT_RATE_LOCKED";
    /** 单位不存在 */
    public static final String UNIT_NOT_FOUND = "UNIT_NOT_FOUND";

    // ===== 库存地点 =====
    /** 地点编码已存在 */
    public static final String WAREHOUSE_CODE_DUPLICATE = "WAREHOUSE_CODE_DUPLICATE";
    /** 地点已被使用，不允许删除 */
    public static final String WAREHOUSE_IN_USE = "WAREHOUSE_IN_USE";
    /** 地点不存在 */
    public static final String WAREHOUSE_NOT_FOUND = "WAREHOUSE_NOT_FOUND";

    // ===== 客户 =====
    /** 客户已关联销售单，不允许删除 */
    public static final String CUSTOMER_IN_USE = "CUSTOMER_IN_USE";
    /** 客户不存在 */
    public static final String CUSTOMER_NOT_FOUND = "CUSTOMER_NOT_FOUND";

    // ===== 供应商 =====
    /** 供应商已关联进货单，不允许删除 */
    public static final String SUPPLIER_IN_USE = "SUPPLIER_IN_USE";
    /** 供应商不存在 */
    public static final String SUPPLIER_NOT_FOUND = "SUPPLIER_NOT_FOUND";

    // ===== 库存 =====
    /** 库存不足 */
    public static final String STOCK_NOT_ENOUGH = "STOCK_NOT_ENOUGH";

    // ===== 进货 =====
    /** 进货明细不能为空 */
    public static final String PURCHASE_ITEM_EMPTY = "PURCHASE_ITEM_EMPTY";

    // ===== 出货 =====
    /** 销售明细不能为空 */
    public static final String SALE_ITEM_EMPTY = "SALE_ITEM_EMPTY";
    /** 非门店地点不允许散卖 */
    public static final String SALE_LOCATION_NOT_STORE = "SALE_LOCATION_NOT_STORE";
    /** 单据状态不允许发货 */
    public static final String SALE_ORDER_STATUS_INVALID = "SALE_ORDER_STATUS_INVALID";
    /** 销售单不存在 */
    public static final String SALE_ORDER_NOT_FOUND = "SALE_ORDER_NOT_FOUND";

    // ===== 财务（第二期）=====
    /** 收款金额超过未收金额 */
    public static final String RECEIPT_OVER_AMOUNT = "RECEIPT_OVER_AMOUNT";
    /** 付款金额超过未付金额 */
    public static final String PAYMENT_OVER_AMOUNT = "PAYMENT_OVER_AMOUNT";
    /** 金额必须大于0 */
    public static final String AMOUNT_INVALID = "AMOUNT_INVALID";
    /** 分类名称已存在 */
    public static final String CATEGORY_NAME_DUPLICATE = "CATEGORY_NAME_DUPLICATE";
    /** 分类已被使用，不允许删除 */
    public static final String CATEGORY_IN_USE = "CATEGORY_IN_USE";
    /** 分类不存在 */
    public static final String CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
    /** 应收账款不存在 */
    public static final String RECEIVABLE_NOT_FOUND = "RECEIVABLE_NOT_FOUND";
    /** 应付账款不存在 */
    public static final String PAYABLE_NOT_FOUND = "PAYABLE_NOT_FOUND";

    // ===== 多租户 / 认证（改造）=====
    /** 账号或密码错误 */
    public static final String AUTH_FAILED = "AUTH_FAILED";
    /** 未登录或登录已过期 */
    public static final String AUTH_UNAUTHENTICATED = "AUTH_UNAUTHENTICATED";
    /** 无权限访问 */
    public static final String AUTH_FORBIDDEN = "AUTH_FORBIDDEN";
    /** 商家已停用 */
    public static final String TENANT_DISABLED = "TENANT_DISABLED";
    /** 商家已到期 */
    public static final String TENANT_EXPIRED = "TENANT_EXPIRED";
    /** 登录名已存在 */
    public static final String LOGIN_NAME_DUPLICATE = "LOGIN_NAME_DUPLICATE";
    /** 商家不存在 */
    public static final String TENANT_NOT_FOUND = "TENANT_NOT_FOUND";
}
