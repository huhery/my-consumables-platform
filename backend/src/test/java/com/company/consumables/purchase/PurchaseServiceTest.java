package com.company.consumables.purchase;

import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.basedata.goods.vo.GoodsUnitSaveVo;
import com.company.consumables.basedata.supplier.service.SupplierService;
import com.company.consumables.basedata.supplier.vo.SupplierSaveVo;
import com.company.consumables.basedata.warehouse.service.WarehouseService;
import com.company.consumables.basedata.warehouse.vo.WarehouseSaveVo;
import com.company.consumables.common.enums.LocationType;
import com.company.consumables.purchase.vo.PurchaseItemVo;
import com.company.consumables.purchase.vo.PurchaseSaveVo;
import com.company.consumables.purchase.service.PurchaseService;
import com.company.consumables.stock.mapper.StockFlowMapper;
import com.company.consumables.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 类描述: 进货入库集成测试，验证单位折算正确性与库存=流水累加不变量
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SupplierService supplierService;
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
     * 功能描述: 整箱进货应按换算率折算为基本单位入库，且库存=流水累加
     *
     * @author honghui
     * @date 2026/06/30 15:20
     */
    @Test
    void createPurchase_withPackagingUnit_shouldConvertAndIncreaseStock() {
        // 准备基础数据：商品（基本单位"个"）+ 包装单位"箱"=24个
        GoodsSaveVo goodsVo = new GoodsSaveVo();
        goodsVo.setSCode("G100");
        goodsVo.setSName("保鲜膜");
        goodsVo.setSBaseUnit("个");
        String goodsId = goodsService.createGoods(goodsVo);

        GoodsUnitSaveVo unitVo = new GoodsUnitSaveVo();
        unitVo.setSUnitName("箱");
        unitVo.setIConvertRate(24);
        goodsService.addUnit(goodsId, unitVo);

        SupplierSaveVo supplierVo = new SupplierSaveVo();
        supplierVo.setSName("供应商A");
        String supplierId = supplierService.createSupplier(supplierVo);

        WarehouseSaveVo whVo = new WarehouseSaveVo();
        whVo.setSCode("WH01");
        whVo.setSName("主仓");
        whVo.setILocationType(LocationType.WAREHOUSE.getCode());
        String warehouseId = warehouseService.createWarehouse(whVo);

        // 进货 5 箱
        PurchaseItemVo item = new PurchaseItemVo();
        item.setSGoodsId(goodsId);
        item.setSInputUnit("箱");
        item.setIInputQty(5);
        item.setIPrice(10);

        PurchaseSaveVo saveVo = new PurchaseSaveVo();
        saveVo.setSSupplierId(supplierId);
        saveVo.setSWarehouseId(warehouseId);
        saveVo.setItems(Collections.singletonList(item));

        String purchaseId = purchaseService.createPurchase(saveVo);
        assertNotNull(purchaseId);

        // 5 箱 × 24 = 120 个
        assertEquals(120, stockService.getQty(warehouseId, goodsId));
        // 库存 = 流水累加
        assertEquals(Integer.valueOf(120), stockFlowMapper.sumChangeQty(goodsId, warehouseId));
        // 总金额 = 进价10 × 120 = 1200
        assertEquals(1200, purchaseService.getDetail(purchaseId).getPurchase().getITotalAmount());
    }
}
