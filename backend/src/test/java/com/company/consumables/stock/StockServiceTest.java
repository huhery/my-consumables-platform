package com.company.consumables.stock;

import com.company.consumables.common.enums.FlowType;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.stock.mapper.StockFlowMapper;
import com.company.consumables.stock.service.StockService;
import com.company.consumables.stock.vo.StockAdjustVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 类描述: 库存内核服务测试，重点校验核心不变量"库存 = 流水累加"与防超卖
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockServiceTest {

    private static final String GOODS = "goods-1";
    private static final String WH = "wh-1";

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
     * 功能描述: 断言库存汇总数量等于流水累加（Property 1 库存一致性）
     *
     * @author honghui
     * @date 2026/06/30 14:30
     */
    private void assertStockEqualsFlowSum() {
        int qty = stockService.getQty(WH, GOODS);
        Integer flowSum = stockFlowMapper.sumChangeQty(GOODS, WH);
        assertEquals(flowSum.intValue(), qty, "库存汇总应等于流水累加之和");
    }

    /**
     * 功能描述: 入库后库存增加，且库存=流水累加
     *
     * @author honghui
     * @date 2026/06/30 14:30
     */
    @Test
    void increaseStock_shouldIncreaseAndKeepInvariant() {
        stockService.increaseStock(WH, GOODS, 100, FlowType.PURCHASE_IN, "P001");
        assertEquals(100, stockService.getQty(WH, GOODS));
        assertStockEqualsFlowSum();

        stockService.increaseStock(WH, GOODS, 50, FlowType.PURCHASE_IN, "P002");
        assertEquals(150, stockService.getQty(WH, GOODS));
        assertStockEqualsFlowSum();
    }

    /**
     * 功能描述: 出库扣减库存，且库存=流水累加
     *
     * @author honghui
     * @date 2026/06/30 14:30
     */
    @Test
    void deductStock_shouldDecreaseAndKeepInvariant() {
        stockService.increaseStock(WH, GOODS, 100, FlowType.PURCHASE_IN, "P001");
        stockService.deductStock(WH, GOODS, 30, FlowType.WHOLESALE_OUT, "S001");
        assertEquals(70, stockService.getQty(WH, GOODS));
        assertStockEqualsFlowSum();
    }

    /**
     * 功能描述: 库存不足时出库应抛业务异常且不改变库存（Property 2 非负库存/防超卖）
     *
     * @author honghui
     * @date 2026/06/30 14:30
     */
    @Test
    void deductStock_notEnough_shouldThrowAndNotChange() {
        stockService.increaseStock(WH, GOODS, 20, FlowType.PURCHASE_IN, "P001");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> stockService.deductStock(WH, GOODS, 50, FlowType.WHOLESALE_OUT, "S001"));
        assertEquals(ErrorCode.STOCK_NOT_ENOUGH, ex.getErrorCode());
        // 库存保持不变
        assertEquals(20, stockService.getQty(WH, GOODS));
        assertStockEqualsFlowSum();
    }

    /**
     * 功能描述: 手工调整（正负）后库存=流水累加
     *
     * @author honghui
     * @date 2026/06/30 14:30
     */
    @Test
    void adjustStock_shouldKeepInvariant() {
        stockService.increaseStock(WH, GOODS, 100, FlowType.PURCHASE_IN, "P001");

        StockAdjustVo minus = new StockAdjustVo();
        minus.setSWarehouseId(WH);
        minus.setSGoodsId(GOODS);
        minus.setIChangeQty(-10);
        minus.setSReason("盘亏");
        stockService.adjustStock(minus);

        assertEquals(90, stockService.getQty(WH, GOODS));
        assertStockEqualsFlowSum();
    }

    /**
     * 功能描述: 无库存记录时负向手工调整应被拒绝
     *
     * @author honghui
     * @date 2026/06/30 14:30
     */
    @Test
    void adjustStock_negativeWhenNoStock_shouldThrow() {
        StockAdjustVo minus = new StockAdjustVo();
        minus.setSWarehouseId(WH);
        minus.setSGoodsId(GOODS);
        minus.setIChangeQty(-5);
        minus.setSReason("异常");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> stockService.adjustStock(minus));
        assertEquals(ErrorCode.STOCK_NOT_ENOUGH, ex.getErrorCode());
    }
}
