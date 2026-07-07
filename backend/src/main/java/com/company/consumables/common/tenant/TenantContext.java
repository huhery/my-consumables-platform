package com.company.consumables.common.tenant;

/**
 * 类描述: 租户上下文，基于 ThreadLocal 存储当前请求所属租户ID
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
public final class TenantContext {

    private TenantContext() {
    }

    /** 当前线程的租户ID存储 */
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * 功能描述: 设置当前线程租户ID
     *
     * @param tenantId 租户ID
     * @author honghui
     * @date 2026/07/12 10:00
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * 功能描述: 获取当前线程租户ID
     *
     * @return 租户ID，可能为 null（如平台管理员或未登录）
     * @author honghui
     * @date 2026/07/12 10:00
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * 功能描述: 清除当前线程租户ID，防止线程复用导致脏数据
     *
     * @author honghui
     * @date 2026/07/12 10:00
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
