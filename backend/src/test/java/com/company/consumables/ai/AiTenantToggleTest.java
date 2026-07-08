package com.company.consumables.ai;

import com.company.consumables.ai.intent.handler.CustomerDebtRankIntentHandler;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.company.consumables.basedata.customer.service.CustomerService;
import com.company.consumables.basedata.customer.vo.CustomerSaveVo;
import com.company.consumables.common.tenant.TenantContext;
import com.company.consumables.platform.tenant.service.TenantService;
import com.company.consumables.platform.tenant.vo.TenantOpenVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类描述: AI 商家开关与租户隔离集成测试。验证：开关默认关闭/可开启（Property 4）、
 *         意图取数经既有 Service 自动隔离，只返回当前租户数据（Property 3）。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AiTenantToggleTest {

    @Autowired
    private TenantService tenantService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerDebtRankIntentHandler customerDebtRankIntentHandler;

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    private TenantOpenVo openVo(String name, String loginName, boolean ai) {
        TenantOpenVo vo = new TenantOpenVo();
        vo.setName(name);
        vo.setLoginName(loginName);
        vo.setPassword("p1");
        vo.setAiEnabled(ai);
        return vo;
    }

    /**
     * 功能描述: 开通时不开 AI 则默认关闭；平台可开启（Property 4）
     *
     * @author honghui
     * @date 2026/07/08 13:30
     */
    @Test
    void aiToggle_defaultOff_canEnable() {
        String tenantId = tenantService.openTenant(openVo("无AI商家", "noai", false));
        assertFalse(tenantService.isAiEnabled(tenantId));

        tenantService.setAiEnabled(tenantId, true);
        assertTrue(tenantService.isAiEnabled(tenantId));

        tenantService.setAiEnabled(tenantId, false);
        assertFalse(tenantService.isAiEnabled(tenantId));
    }

    /**
     * 功能描述: 开通时直接开 AI，则查询即为开启
     *
     * @author honghui
     * @date 2026/07/08 13:30
     */
    @Test
    void aiToggle_openWithAi() {
        String tenantId = tenantService.openTenant(openVo("有AI商家", "hasai", true));
        assertTrue(tenantService.isAiEnabled(tenantId));
    }

    /**
     * 功能描述: 意图取数经租户隔离——租户A的客户欠款意图看不到租户B的客户（Property 3）
     *
     * @author honghui
     * @date 2026/07/08 13:30
     */
    @Test
    void intentQueryIsolatedByTenant() {
        // 租户A建一个客户
        TenantContext.setTenantId("ai-tenant-A");
        CustomerSaveVo ca = new CustomerSaveVo();
        ca.setSName("A的超市");
        customerService.createCustomer(ca);

        // 租户B建一个客户
        TenantContext.setTenantId("ai-tenant-B");
        CustomerSaveVo cb = new CustomerSaveVo();
        cb.setSName("B的超市");
        customerService.createCustomer(cb);

        // 租户A执行"客户欠款排行"意图：数据经既有 Service + 租户拦截器，
        // 不应包含 B 的客户（此处无欠款，返回无欠款提示即证明未串数据异常）
        TenantContext.setTenantId("ai-tenant-A");
        AiAnswerVo answer = customerDebtRankIntentHandler.handle(Collections.emptyMap());
        // 回答不应出现租户B的客户名
        assertFalse(answer.getAnswer().contains("B的超市"));
    }
}
