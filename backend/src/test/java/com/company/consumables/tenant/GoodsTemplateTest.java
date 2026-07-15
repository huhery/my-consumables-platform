package com.company.consumables.tenant;

import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.tenant.TenantContext;
import com.company.consumables.platform.tenant.service.TenantService;
import com.company.consumables.platform.tenant.vo.TenantOpenVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 类描述: 产品模板复制测试。验证开通商家后自动初始化产品目录。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/15
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GoodsTemplateTest {

    @Autowired
    private TenantService tenantService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private com.company.consumables.basedata.goods.mapper.GoodsTemplateMapper goodsTemplateMapper;

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    /**
     * 功能描述: 开通商家后，新商家登录应看到模板中的产品（3条模板→3条商品）
     *
     * @author honghui
     * @date 2026/07/15 21:00
     */
    @Test
    void openTenant_shouldCopyGoodsTemplate() {
        // 先确认模板表有数据（诊断）
        TenantContext.clear(); // 平台管理员无租户上下文
        java.util.List<?> templates = goodsTemplateMapper.selectAll();
        assertEquals(3, templates.size(), "模板表应有3条测试数据");

        TenantOpenVo vo = new TenantOpenVo();
        vo.setName("模板测试商家");
        vo.setLoginName("template_test");
        vo.setPassword("p123");
        String tenantId = tenantService.openTenant(vo);

        // 切换到新商家上下文查商品
        TenantContext.setTenantId(tenantId);
        GoodsQueryVo query = new GoodsQueryVo();
        query.setPageSize(100);
        PageResult<?> result = goodsService.pageGoods(query);

        // 应有 3 条模板产品被复制
        assertEquals(3, result.getTotal(), "开通商家后应自动复制模板产品");
    }
}
