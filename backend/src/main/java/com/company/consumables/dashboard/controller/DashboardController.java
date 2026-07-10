package com.company.consumables.dashboard.controller;

import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.dashboard.service.DashboardService;
import com.company.consumables.dashboard.vo.DashboardSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类描述: 工作台首页 REST 接口。需登录（JWT 过滤器保证），数据自动按当前租户隔离。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 功能描述: 获取工作台聚合数据
     *
     * @return 工作台聚合数据
     * @author honghui
     * @date 2026/07/08 14:10
     */
    @GetMapping("/summary")
    public RestApiResultVo<DashboardSummaryVo> summary() {
        return RestApiResultVo.ok(dashboardService.summary());
    }
}
