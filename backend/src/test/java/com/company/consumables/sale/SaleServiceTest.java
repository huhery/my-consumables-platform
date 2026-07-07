package com.company.consumables.sale;

import com.company.consumables.basedata.customer.service.CustomerService;
import com.company.consumables.basedata.customer.vo.CustomerSaveVo;
import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.basedata.goods.vo.GoodsUnitSaveVo;
import com.company.consumables.basedata.warehouse.service.WarehouseService;
import com.company.consumables.basedata.warehouse.vo.WarehouseSaveVo;
import com.company.consumables.common.enums.FlowType;
import com.company.consumables.common.enums.LocationType;
import com.company.consumables.common.enums.SaleStatus;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.sale.service.SaleService;
import com.company.consumables.sale.vo.SaleItemVo;
import com.company.consumables.sale.vo.StoreSaleSaveVo;
import com.company.consumables.sale.vo.WholesaleSaveVo;
import com.company.consumables.stock.mapper.StockFlowMapper;
import com.company.consumables.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 类描述: 出货集成测试，覆盖批发发货防超卖、门店类型约束与库存=流水累加不变量
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SaleServiceTest {

    @Autowired
    private SaleService saleService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockFlowMapper stockFlowMapper;

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
     * 功能描述: 创建商品（基本单位"个"，含包装单位"箱"=24）
     *
     * @param code 编码
     * @return 商品ID
     * @author honghui
     * @date 2026/06/30 16:20
     */
    private String createGoods(String code) {
        GoodsSaveVo goodsVo = new GoodsSaveVo();
        goodsVo.setSCode(code);
        goodsVo.setSName("保鲜膜");
        goodsVo.setSBaseUnit("个");
        String goodsId = goodsService.createGoods(goodsVo);
        GoodsUnitSaveVo unitVo = new GoodsUnitSaveVo();
        unitVo.setSUnitName("箱");
        unitVo.setIConvertRate(24);
        goodsService.addUnit(goodsId, unitVo);
        return goodsId;
    }

    /**
     * 功能描述: 创建库存地点
     *
     * @param code 编码
     * @param type 地点类型
     * @return 地点ID
     * @author honghui
     * @date 2026/06/30 16:20
     */
    private String createWarehouse(String code, LocationType type) {
        WarehouseSaveVo whVo = new WarehouseSaveVo();
        whVo.setSCode(code);
        whVo.setSName(code);
        whVo.setILocationType(type.getCode());
        return warehouseService.createWarehouse(whVo);
    }

    /**
     * 功能描述: 创建客户
     *
     * @return 客户ID
     * @author honghui
     * @date 2026/06/30 16:20
     */
    private String createCustomer() {
        CustomerSaveVo vo = new CustomerSaveVo();
        vo.setSName("超市A");
        return customerService.createCustomer(vo);
    }

    /**
     * 功能描述: 构造销售明细
     *
     * @param goodsId 商品ID
     * @param unit    单位
     * @param qty     数量
     * @return 明细
     * @author honghui
     * @date 2026/06/30 16:20
     */
    private SaleItemVo item(String goodsId, String unit, int qty) {
        SaleItemVo item = new SaleItemVo();
        item.setSGoodsId(goodsId);
        item.setSInputUnit(unit);
        item.setIInputQty(qty);
        item.setIPrice(10);
        return item;
    }

    /**
     * 功能描述: 批发发货成功后库存扣减且库存=流水累加
     *
     * @author honghui
     * @date 2026/06/30 16:20
     */
    @Test
    void wholesaleShip_success_shouldDeductAndKeepInvariant() {
        String goodsId = createGoods("G200");
        String whId = createWarehouse("WH01", LocationType.WAREHOUSE);
        String customerId = createCustomer();
        // 先入库 10 箱 = 240 个
        stockService.increaseStock(whId, goodsId, 240, FlowType.PURCHASE_IN, "P1");

        // 批发 5 箱 = 120 个
        WholesaleSaveVo vo = new WholesaleSaveVo();
        vo.setSCustomerId(customerId);
        vo.setSWarehouseId(whId);
        vo.setItems(Collections.singletonList(item(goodsId, "箱", 5)));
        String saleId = saleService.createWholesaleOrder(vo);

        // 录单后未扣库存
        assertEquals(240, stockService.getQty(whId, goodsId));
        assertEquals(SaleStatus.PENDING.getCode(),
                saleService.getDetail(saleId).getSaleOrder().getIStatus());

        // 发货后扣减
        saleService.ship(saleId);
        assertEquals(120, stockService.getQty(whId, goodsId));
        assertEquals(Integer.valueOf(120), stockFlowMapper.sumChangeQty(goodsId, whId));
        assertEquals(SaleStatus.COMPLETED.getCode(),
                saleService.getDetail(saleId).getSaleOrder().getIStatus());
    }

    /**
     * 功能描述: 库存不足时发货应抛业务异常且库存不变（Property 2 防超卖）
     *
     * @author honghui
     * @date 2026/06/30 16:20
     */
    @Test
    void wholesaleShip_notEnough_shouldThrow() {
        String goodsId = createGoods("G201");
        String whId = createWarehouse("WH02", LocationType.WAREHOUSE);
        String customerId = createCustomer();
        // 入库仅 1 箱 = 24 个
        stockService.increaseStock(whId, goodsId, 24, FlowType.PURCHASE_IN, "P1");

        WholesaleSaveVo vo = new WholesaleSaveVo();
        vo.setSCustomerId(customerId);
        vo.setSWarehouseId(whId);
        vo.setItems(Collections.singletonList(item(goodsId, "箱", 5)));
        String saleId = saleService.createWholesaleOrder(vo);

        BusinessException ex = assertThrows(BusinessException.class, () -> saleService.ship(saleId));
        assertEquals(ErrorCode.STOCK_NOT_ENOUGH, ex.getErrorCode());
        // 库存保持不变
        assertEquals(24, stockService.getQty(whId, goodsId));
    }

    /**
     * 功能描述: 非门店地点散卖应被拒绝（Property 6 门店约束）
     *
     * @author honghui
     * @date 2026/06/30 16:20
     */
    @Test
    void storeSale_nonStore_shouldThrow() {
        String goodsId = createGoods("G202");
        String whId = createWarehouse("WH03", LocationType.WAREHOUSE);
        stockService.increaseStock(whId, goodsId, 100, FlowType.PURCHASE_IN, "P1");

        StoreSaleSaveVo vo = new StoreSaleSaveVo();
        vo.setSWarehouseId(whId);
        vo.setItems(Collections.singletonList(item(goodsId, "个", 10)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> saleService.createStoreSale(vo));
        assertEquals(ErrorCode.SALE_LOCATION_NOT_STORE, ex.getErrorCode());
    }

    /**
     * 功能描述: 门店散卖成功后扣减门店库存且库存=流水累加
     *
     * @author honghui
     * @date 2026/06/30 16:20
     */
    @Test
    void storeSale_success_shouldDeductAndKeepInvariant() {
        String goodsId = createGoods("G203");
        String storeId = createWarehouse("ST01", LocationType.STORE);
        stockService.increaseStock(storeId, goodsId, 100, FlowType.PURCHASE_IN, "P1");

        StoreSaleSaveVo vo = new StoreSaleSaveVo();
        vo.setSWarehouseId(storeId);
        vo.setItems(Collections.singletonList(item(goodsId, "个", 30)));
        saleService.createStoreSale(vo);

        assertEquals(70, stockService.getQty(storeId, goodsId));
        assertEquals(Integer.valueOf(70), stockFlowMapper.sumChangeQty(goodsId, storeId));
    }

    /**
     * 功能描述: 门店库存不足时散卖应被拒绝
     *
     * @author honghui
     * @date 2026/06/30 16:20
     */
    @Test
    void storeSale_notEnough_shouldThrow() {
        String goodsId = createGoods("G204");
        String storeId = createWarehouse("ST02", LocationType.STORE);
        stockService.increaseStock(storeId, goodsId, 5, FlowType.PURCHASE_IN, "P1");

        StoreSaleSaveVo vo = new StoreSaleSaveVo();
        vo.setSWarehouseId(storeId);
        vo.setItems(Collections.singletonList(item(goodsId, "个", 10)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> saleService.createStoreSale(vo));
        assertEquals(ErrorCode.STOCK_NOT_ENOUGH, ex.getErrorCode());
    }
}
