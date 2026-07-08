package com.company.consumables.platform.tenant.service;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.platform.tenant.entity.Tenant;
import com.company.consumables.platform.tenant.vo.TenantOpenVo;
import com.company.consumables.platform.tenant.vo.TenantQueryVo;

/**
 * 类描述: 平台租户（商家）管理服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
public interface TenantService {

    /**
     * 功能描述: 开通商家（建租户 + 初始商家账号），同一事务
     *
     * @param vo 开通入参
     * @return 租户ID
     * @author honghui
     * @date 2026/07/12 12:40
     */
    String openTenant(TenantOpenVo vo);

    /**
     * 功能描述: 启用商家
     *
     * @param tenantId 租户ID
     * @author honghui
     * @date 2026/07/12 12:40
     */
    void enable(String tenantId);

    /**
     * 功能描述: 停用商家
     *
     * @param tenantId 租户ID
     * @author honghui
     * @date 2026/07/12 12:40
     */
    void disable(String tenantId);

    /**
     * 功能描述: 续期商家（从当前到期日延长 N 年）
     *
     * @param tenantId 租户ID
     * @param years    续期年数
     * @author honghui
     * @date 2026/07/12 15:00
     */
    void renew(String tenantId, int years);

    /**
     * 功能描述: 设置商家 AI 开关（开通/关闭智能问数）
     *
     * @param tenantId 租户ID
     * @param enabled  是否开通
     * @author honghui
     * @date 2026/07/08 10:10
     */
    void setAiEnabled(String tenantId, boolean enabled);

    /**
     * 功能描述: 判断商家是否已开通 AI
     *
     * @param tenantId 租户ID
     * @return true 已开通
     * @author honghui
     * @date 2026/07/08 10:10
     */
    boolean isAiEnabled(String tenantId);

    /**
     * 功能描述: 分页查询商家
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/12 12:40
     */
    PageResult<Tenant> pageTenant(TenantQueryVo query);
}
