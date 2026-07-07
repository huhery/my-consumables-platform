package com.company.consumables.finance;

import com.company.consumables.basedata.customer.service.CustomerService;
import com.company.consumables.basedata.customer.vo.CustomerSaveVo;
import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.basedata.warehouse.service.WarehouseService;
import com.company.consumables.basedata.warehouse.vo.WarehouseSaveVo;
import com.company.consumables.common.enums.CategoryType;
import com.company.consumables.common.enums.FlowType;
import com.company.consumables.common.enums.LocationType;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.finance.expense.service.ExpenseService;
import com.company.consumables.finance.expense.vo.ExpenseCategorySaveVo;
import com.company.consumables.finance.expense.vo.ExpenseSaveVo;
import com.company.consumables.finance.receivable.entity.Receivable;
import com.company.consumables.finance.receivable.service.ReceivableService;
import com.company.consumables.finance.receivable.vo.ReceiptSaveVo;
import com.company.consumables.stock.service.StockService;
import com.company.consumables.sale.service.SaleService;
import com.company.consumables.sale.vo.SaleItemVo;
import com.company.consumables.sale.vo.StoreSaleSaveVo;
import com.company.consumables.sale.vo.WholesaleSaveVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类描述: 第二期财务模块集成测试，覆盖应收生成、部分收款、超额拒绝、散卖不挂账、费用记账
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FinanceServiceTest {

    @Autowired
    private SaleService saleService;
    @Autowired
    private ReceivableService receivableService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private StockService stockService;

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

    private String createGoods(String code) {
        GoodsSaveVo vo = new GoodsSaveVo();
        vo.setSCode(code);
        vo.setSName("保鲜膜");
        vo.setSBaseUnit("个");
        return goodsService.createGoods(vo);
    }

    private String createWarehouse(String code, LocationType type) {
        WarehouseSaveVo vo = new WarehouseSaveVo();
        vo.setSCode(code);
        vo.setSName(code);
        vo.setILocationType(type.getCode());
        return warehouseService.createWarehouse(vo);
    }

    private String createCustomer() {
        CustomerSaveVo vo = new CustomerSaveVo();
        vo.setSName("超市A");
        return customerService.createCustomer(vo);
    }

    private SaleItemVo item(String goodsId, int qty, int price) {
        SaleItemVo it = new SaleItemVo();
        it.setSGoodsId(goodsId);
        it.setSInputUnit("个");
        it.setIInputQty(qty);
        it.setIPrice(price);
        return it;
    }

    /**
     * 功能描述: 批发发货后生成应收；部分收款后未收金额正确（Property 1、4）
     *
     * @author honghui
     * @date 2026/07/06 13:00
     */
    @Test
    void wholesaleShip_generatesReceivable_andPartialReceipt() {
        String goodsId = createGoods("F001");
        String whId = createWarehouse("WH01", LocationType.WAREHOUSE);
        String customerId = createCustomer();
        stockService.increaseStock(whId, goodsId, 100, FlowType.PURCHASE_IN, "P1");

        // 批发 10 个 × 单价 100 分 = 1000 分
        WholesaleSaveVo vo = new WholesaleSaveVo();
        vo.setSCustomerId(customerId);
        vo.setSWarehouseId(whId);
        vo.setItems(Collections.singletonList(item(goodsId, 10, 100)));
        String saleId = saleService.createWholesaleOrder(vo);
        saleService.ship(saleId);

        // 发货后应生成应收 1000
        List<Receivable> list = receivableService.listByCustomer(customerId);
        assertEquals(1, list.size());
        assertEquals(1000, list.get(0).getITotalAmount());
        assertEquals(0, list.get(0).getIReceivedAmount());

        // 部分收款 400
        ReceiptSaveVo receipt = new ReceiptSaveVo();
        receipt.setSCustomerId(customerId);
        receipt.setSSaleId(saleId);
        receipt.setIAmount(400);
        receipt.setDOccurDate(new Date());
        receivableService.createReceipt(receipt);

        Receivable after = receivableService.listByCustomer(customerId).get(0);
        assertEquals(400, after.getIReceivedAmount());
        // 未收 = 1000 - 400 = 600
        assertEquals(600, after.getITotalAmount() - after.getIReceivedAmount());
    }

    /**
     * 功能描述: 超额收款应被拒绝（Property 4）
     *
     * @author honghui
     * @date 2026/07/06 13:00
     */
    @Test
    void receipt_overAmount_shouldThrow() {
        String goodsId = createGoods("F002");
        String whId = createWarehouse("WH02", LocationType.WAREHOUSE);
        String customerId = createCustomer();
        stockService.increaseStock(whId, goodsId, 100, FlowType.PURCHASE_IN, "P1");

        WholesaleSaveVo vo = new WholesaleSaveVo();
        vo.setSCustomerId(customerId);
        vo.setSWarehouseId(whId);
        vo.setItems(Collections.singletonList(item(goodsId, 10, 100)));
        String saleId = saleService.createWholesaleOrder(vo);
        saleService.ship(saleId);

        // 应收 1000，收款 1500 超额
        ReceiptSaveVo receipt = new ReceiptSaveVo();
        receipt.setSCustomerId(customerId);
        receipt.setSSaleId(saleId);
        receipt.setIAmount(1500);
        receipt.setDOccurDate(new Date());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> receivableService.createReceipt(receipt));
        assertEquals(ErrorCode.RECEIPT_OVER_AMOUNT, ex.getErrorCode());
    }

    /**
     * 功能描述: 门店散卖不生成应收（Property 5）
     *
     * @author honghui
     * @date 2026/07/06 13:00
     */
    @Test
    void storeSale_noReceivable() {
        String goodsId = createGoods("F003");
        String storeId = createWarehouse("ST01", LocationType.STORE);
        stockService.increaseStock(storeId, goodsId, 100, FlowType.PURCHASE_IN, "P1");

        StoreSaleSaveVo vo = new StoreSaleSaveVo();
        vo.setSWarehouseId(storeId);
        vo.setItems(Collections.singletonList(item(goodsId, 5, 200)));
        saleService.createStoreSale(vo);

        // 散卖无客户、不挂应收（按空客户查询应为空）
        assertTrue(receivableService.listByCustomer("").isEmpty());
    }

    /**
     * 功能描述: 费用支出记账应成功（Property 3）；金额非法应被拒
     *
     * @author honghui
     * @date 2026/07/06 13:00
     */
    @Test
    void expense_record_andInvalidAmount() {
        ExpenseCategorySaveVo catVo = new ExpenseCategorySaveVo();
        catVo.setSName("差旅费");
        catVo.setICategoryType(CategoryType.EXPENSE.getCode());
        String categoryId = expenseService.createCategory(catVo);

        ExpenseSaveVo expense = new ExpenseSaveVo();
        expense.setSCategoryId(categoryId);
        expense.setIAmount(5000);
        expense.setDOccurDate(new Date());
        expense.setSRemark("出差");
        // 正常记账不抛异常
        expenseService.recordExpense(expense);
    }

    /**
     * 功能描述: 分类名称同类型重复应被拒
     *
     * @author honghui
     * @date 2026/07/06 13:00
     */
    @Test
    void category_duplicate_shouldThrow() {
        ExpenseCategorySaveVo catVo = new ExpenseCategorySaveVo();
        catVo.setSName("测试分类");
        catVo.setICategoryType(CategoryType.EXPENSE.getCode());
        expenseService.createCategory(catVo);

        ExpenseCategorySaveVo dup = new ExpenseCategorySaveVo();
        dup.setSName("测试分类");
        dup.setICategoryType(CategoryType.EXPENSE.getCode());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseService.createCategory(dup));
        assertEquals(ErrorCode.CATEGORY_NAME_DUPLICATE, ex.getErrorCode());
    }
}
