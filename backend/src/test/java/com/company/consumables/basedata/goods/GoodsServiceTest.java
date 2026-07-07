package com.company.consumables.basedata.goods;

import com.company.consumables.basedata.goods.entity.Goods;
import com.company.consumables.basedata.goods.mapper.GoodsMapper;
import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.basedata.goods.vo.GoodsUnitSaveVo;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类描述: 商品档案与单位换算服务测试
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GoodsServiceTest {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 功能描述: 每个测试前设置默认租户上下文，适配多租户隔离
     *
     * @author honghui
     * @date 2026/07/12 13:20
     */
    @org.junit.jupiter.api.BeforeEach
    void setUpTenant() {
        com.company.consumables.common.tenant.TenantContext.setTenantId("test-tenant");
    }

    /**
     * 功能描述: 每个测试后清理租户上下文
     *
     * @author honghui
     * @date 2026/07/12 13:20
     */
    @org.junit.jupiter.api.AfterEach
    void clearTenant() {
        com.company.consumables.common.tenant.TenantContext.clear();
    }

    /**
     * 功能描述: 构造商品入参
     *
     * @param code 编码
     * @param name 名称
     * @return 入参
     * @author honghui
     * @date 2026/06/30 12:00
     */
    private GoodsSaveVo buildGoods(String code, String name) {
        GoodsSaveVo vo = new GoodsSaveVo();
        vo.setSCode(code);
        vo.setSName(name);
        vo.setSCategory("耗材");
        vo.setSSpec("标准");
        vo.setSBaseUnit("个");
        return vo;
    }

    /**
     * 功能描述: 新增商品后应能查询到，且审计字段被自动填充
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void createGoods_shouldPersistWithAuditFields() {
        String id = goodsService.createGoods(buildGoods("G001", "保鲜膜"));
        assertNotNull(id);

        Goods saved = goodsMapper.selectById(id);
        assertNotNull(saved);
        assertEquals("G001", saved.getSCode());
        // 审计字段由 MyBatis 拦截器自动填充
        assertNotNull(saved.getDtCreateTime());
        assertNotNull(saved.getDtUpdateTime());
        assertEquals("test-user", saved.getSCreateUser());
        assertEquals("test-user", saved.getSUpdateUser());
    }

    /**
     * 功能描述: 商品编码重复时应抛出业务异常
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void createGoods_duplicateCode_shouldThrow() {
        goodsService.createGoods(buildGoods("G002", "购物袋"));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> goodsService.createGoods(buildGoods("G002", "购物袋2")));
        assertEquals(ErrorCode.GOODS_CODE_DUPLICATE, ex.getErrorCode());
    }

    /**
     * 功能描述: 分页查询应按名称模糊命中
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void pageGoods_shouldFilterByName() {
        goodsService.createGoods(buildGoods("G003", "收银纸卷"));
        goodsService.createGoods(buildGoods("G004", "清洁布"));

        GoodsQueryVo query = new GoodsQueryVo();
        query.setSName("收银");
        PageResult<Goods> page = goodsService.pageGoods(query);
        assertEquals(1, page.getTotal());
        assertEquals("G003", page.getList().get(0).getSCode());
    }

    /**
     * 功能描述: 包装单位换算率非法（<=0）时应抛出业务异常
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void addUnit_invalidRate_shouldThrow() {
        String goodsId = goodsService.createGoods(buildGoods("G005", "保鲜膜大卷"));
        GoodsUnitSaveVo unit = new GoodsUnitSaveVo();
        unit.setSUnitName("箱");
        unit.setIConvertRate(0);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> goodsService.addUnit(goodsId, unit));
        assertEquals(ErrorCode.UNIT_RATE_INVALID, ex.getErrorCode());
    }

    /**
     * 功能描述: 正常配置包装单位后应能查询到
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void addUnit_valid_shouldPersist() {
        String goodsId = goodsService.createGoods(buildGoods("G006", "保鲜膜小卷"));
        GoodsUnitSaveVo unit = new GoodsUnitSaveVo();
        unit.setSUnitName("箱");
        unit.setIConvertRate(24);
        String unitId = goodsService.addUnit(goodsId, unit);
        assertNotNull(unitId);
        assertEquals(1, goodsService.listUnits(goodsId).size());
        assertEquals(24, goodsService.listUnits(goodsId).get(0).getIConvertRate());
    }

    /**
     * 功能描述: 删除不存在的商品应抛出业务异常
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void deleteGoods_notFound_shouldThrow() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> goodsService.deleteGoods("not-exist-id"));
        assertEquals(ErrorCode.GOODS_NOT_FOUND, ex.getErrorCode());
    }

    /**
     * 功能描述: 修改商品基本单位且无库存/流水时应成功
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void updateGoods_changeBaseUnit_whenNotInUse_shouldSucceed() {
        String id = goodsService.createGoods(buildGoods("G007", "纸杯"));
        GoodsSaveVo update = buildGoods("G007", "纸杯");
        update.setSId(id);
        update.setSBaseUnit("只");
        goodsService.updateGoods(update);
        assertEquals("只", goodsMapper.selectById(id).getSBaseUnit());
    }

    /**
     * 功能描述: 分页偏移计算应正确
     *
     * @author honghui
     * @date 2026/06/30 12:00
     */
    @Test
    void pageQuery_offset_shouldBeCorrect() {
        GoodsQueryVo query = new GoodsQueryVo();
        query.setPageNum(3);
        query.setPageSize(10);
        assertEquals(20, query.getOffset());
        assertTrue(query.getLimit() > 0);
    }
}
