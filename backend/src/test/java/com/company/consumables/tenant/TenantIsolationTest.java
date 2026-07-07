package com.company.consumables.tenant;

import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.common.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 类描述: 多租户数据隔离测试，验证租户间数据互不可见、租户内编码可重复（Property 1、3）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TenantIsolationTest {

    private static final String TENANT_A = "tenant-A";
    private static final String TENANT_B = "tenant-B";

    @Autowired
    private GoodsService goodsService;

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    private GoodsSaveVo goods(String code, String name) {
        GoodsSaveVo vo = new GoodsSaveVo();
        vo.setSCode(code);
        vo.setSName(name);
        vo.setSBaseUnit("个");
        return vo;
    }

    /**
     * 功能描述: 租户A新增的商品，租户B查询不到（Property 1 隔离）
     *
     * @author honghui
     * @date 2026/07/12 13:40
     */
    @Test
    void tenantCannotSeeOtherTenantData() {
        // 租户A建两个商品
        TenantContext.setTenantId(TENANT_A);
        goodsService.createGoods(goods("A001", "租户A商品1"));
        goodsService.createGoods(goods("A002", "租户A商品2"));

        // 租户A能查到自己的 2 个
        GoodsQueryVo queryA = new GoodsQueryVo();
        assertEquals(2, goodsService.pageGoods(queryA).getTotal());

        // 切换到租户B，查不到租户A的数据
        TenantContext.setTenantId(TENANT_B);
        GoodsQueryVo queryB = new GoodsQueryVo();
        assertEquals(0, goodsService.pageGoods(queryB).getTotal());

        // 租户B建自己的商品，只看到自己的
        goodsService.createGoods(goods("B001", "租户B商品1"));
        assertEquals(1, goodsService.pageGoods(queryB).getTotal());
    }

    /**
     * 功能描述: 不同租户可使用相同商品编码（Property 3 租户内唯一）
     *
     * @author honghui
     * @date 2026/07/12 13:40
     */
    @Test
    void differentTenantsCanUseSameCode() {
        TenantContext.setTenantId(TENANT_A);
        String idA = goodsService.createGoods(goods("SAME", "A的商品"));

        TenantContext.setTenantId(TENANT_B);
        // 相同编码 SAME 在租户B下应允许创建（租户内唯一，非全局）
        String idB = goodsService.createGoods(goods("SAME", "B的商品"));

        // 两个不同的记录
        org.junit.jupiter.api.Assertions.assertNotEquals(idA, idB);
        // 租户B只看到自己那条
        GoodsQueryVo query = new GoodsQueryVo();
        query.setSCode("SAME");
        assertEquals(1, goodsService.pageGoods(query).getTotal());
    }
}
