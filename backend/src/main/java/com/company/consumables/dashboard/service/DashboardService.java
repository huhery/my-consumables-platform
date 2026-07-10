package com.company.consumables.dashboard.service;

import com.company.consumables.dashboard.vo.DashboardSummaryVo;

/**
 * 类描述: 工作台首页聚合服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
public interface DashboardService {

    /**
     * 功能描述: 聚合工作台首页所需的全部只读汇总数据（复用既有 Service，自动经租户隔离）
     *
     * @return 工作台聚合数据
     * @author honghui
     * @date 2026/07/08 14:00
     */
    DashboardSummaryVo summary();
}
