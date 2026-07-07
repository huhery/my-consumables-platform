package com.company.consumables.common.context;

/**
 * 类描述: 当前登录用户上下文，基于 ThreadLocal 存储。第一期免登录，默认用户由配置提供
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public final class UserContext {

    private UserContext() {
    }

    /** 当前线程的用户标识存储 */
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    /**
     * 功能描述: 设置当前线程用户
     *
     * @param userId 用户标识
     * @author honghui
     * @date 2026/06/30 10:50
     */
    public static void setCurrentUser(String userId) {
        CURRENT_USER.set(userId);
    }

    /**
     * 功能描述: 获取当前线程用户
     *
     * @return 用户标识，可能为 null
     * @author honghui
     * @date 2026/06/30 10:50
     */
    public static String getCurrentUser() {
        return CURRENT_USER.get();
    }

    /**
     * 功能描述: 清除当前线程用户，防止线程复用导致的脏数据
     *
     * @author honghui
     * @date 2026/06/30 10:50
     */
    public static void clear() {
        CURRENT_USER.remove();
    }
}
