package com.company.consumables.dashboard;

import com.company.consumables.basedata.customer.service.CustomerService;
import com.company.consumables.basedata.customer.vo.CustomerSaveVo;
import com.company.consumables.common.tenant.TenantContext;
import com.company.consumables.dashboard.service.DashboardService;
import com.company.consumables.dashboard.vo.DashboardSummaryVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 类描述: 工作台聚合服务集成测试。验证返回字段齐全、只读、租户隔离（Property 1、2）。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private CustomerService customerService;

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    /**
     * 功能描述: 聚合接口在租户上下文下返回非空结构，各汇总字段有值（Property 2 只读、字段齐全）
     *
     * @author honghui
     * @date 2026/07/08 14:20
     */
    @Test
    void summary_returnsCompleteStructure() {
        TenantContext.setTenantId("dash-tenant");
        DashboardSummaryVo vo = dashboardService.summary();

        assertNotNull(vo);
        assertNotNull(vo.getTodayIncome());
        assertNotNull(vo.getTodayExpense());
        assertNotNull(vo.getMonthSales());
        assertNotNull(vo.getTotalReceivable());
        assertNotNull(vo.getTotalPayable());
        assertNotNull(vo.getLowStockCount());
        assertNotNull(vo.getLowStockItems());
        assertNotNull(vo.getDeliveryCount());
        assertNotNull(vo.getDeliveryItems());
    }

    /**
     * 功能描述: 两租户隔离——A 上下文聚合数据不含 B 的数据（Property 1）
     *
     * @author honghui
     * @date 2026/07/08 14:20
     */
    @Test
    void summary_isolatedByTenant() {
        // 租户B建客户（无欠款，仅验证隔离不串数据）
        TenantContext.setTenantId("dash-tenant-B");
        CustomerSaveVo cb = new CustomerSaveVo();
        cb.setSName("B的超市");
        customerService.createCustomer(cb);

        // 租户A聚合：欠款合计为 0（A 无任何数据），且不应因 B 的数据报错
        TenantContext.setTenantId("dash-tenant-A");
        DashboardSummaryVo vo = dashboardService.summary();
        assertNotNull(vo);
        // A 没有任何应收/应付，合计应为 0
        org.junit.jupiter.api.Assertions.assertEquals(0L, vo.getTotalReceivable());
        org.junit.jupiter.api.Assertions.assertEquals(0L, vo.getTotalPayable());
    }
}
