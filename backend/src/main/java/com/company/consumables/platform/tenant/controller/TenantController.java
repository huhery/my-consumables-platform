package com.company.consumables.platform.tenant.controller;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.platform.tenant.entity.Tenant;
import com.company.consumables.platform.tenant.service.TenantService;
import com.company.consumables.platform.tenant.vo.TenantOpenVo;
import com.company.consumables.platform.tenant.vo.TenantQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类描述: 平台租户（商家）管理 REST 接口（仅平台管理员，鉴权由拦截器控制）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@RestController
@RequestMapping("/api/platform/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 功能描述: 开通商家
     *
     * @param vo 开通入参
     * @return 租户ID
     * @author honghui
     * @date 2026/07/12 12:50
     */
    @PostMapping
    public RestApiResultVo<String> open(@RequestBody @Valid TenantOpenVo vo) {
        return RestApiResultVo.ok(tenantService.openTenant(vo));
    }

    /**
     * 功能描述: 分页查询商家
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/12 12:50
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<Tenant>> page(TenantQueryVo query) {
        return RestApiResultVo.ok(tenantService.pageTenant(query));
    }

    /**
     * 功能描述: 启用商家
     *
     * @param id 租户ID
     * @return 空结果
     * @author honghui
     * @date 2026/07/12 12:50
     */
    @PostMapping("/{id}/enable")
    public RestApiResultVo<Void> enable(@PathVariable("id") String id) {
        tenantService.enable(id);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 续期商家
     *
     * @param id    租户ID
     * @param years 续期年数（query param）
     * @return 空结果
     * @author honghui
     * @date 2026/07/12 15:10
     */
    @PostMapping("/{id}/renew")
    public RestApiResultVo<Void> renew(@PathVariable("id") String id,
                                       @RequestParam(value = "years", defaultValue = "1") int years) {
        tenantService.renew(id, years);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 停用商家
     *
     * @param id 租户ID
     * @return 空结果
     * @author honghui
     * @date 2026/07/12 12:50
     */
    @PostMapping("/{id}/disable")
    public RestApiResultVo<Void> disable(@PathVariable("id") String id) {
        tenantService.disable(id);
        return RestApiResultVo.ok();
    }
}
