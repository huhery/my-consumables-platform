package com.company.consumables.sale.service.impl;

import com.company.consumables.basedata.customer.entity.Customer;
import com.company.consumables.basedata.customer.service.CustomerService;
import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.warehouse.entity.Warehouse;
import com.company.consumables.basedata.warehouse.service.WarehouseService;
import com.company.consumables.common.constant.Constant;
import com.company.consumables.common.context.UserContext;
import com.company.consumables.common.enums.FlowType;
import com.company.consumables.common.enums.LocationType;
import com.company.consumables.common.enums.SaleStatus;
import com.company.consumables.common.enums.SaleType;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.util.BizNoGenerator;
import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.entity.SaleOrderItem;
import com.company.consumables.sale.mapper.SaleOrderItemMapper;
import com.company.consumables.sale.mapper.SaleOrderMapper;
import com.company.consumables.sale.service.SaleService;
import com.company.consumables.sale.vo.SaleDetailVo;
import com.company.consumables.sale.vo.SaleItemVo;
import com.company.consumables.sale.vo.SaleQueryVo;
import com.company.consumables.sale.vo.StoreSaleSaveVo;
import com.company.consumables.sale.vo.WholesaleSaveVo;
import com.company.consumables.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 类描述: 出货服务实现。批发先录单后发货扣库存；门店散卖保存即出库。两者共用出库内核 deductStock
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    /** 免登录场景默认操作人 */
    @Value("${consumables.audit.default-user:admin}")
    private String defaultUser;

    /** 期望送达日期未填时的占位日期（与数据库默认一致） */
    private static final Date UNFILLED_DATE = new Date(0L);

    private final SaleOrderMapper saleOrderMapper;
    private final SaleOrderItemMapper saleOrderItemMapper;
    private final GoodsService goodsService;
    private final CustomerService customerService;
    private final WarehouseService warehouseService;
    private final StockService stockService;
    private final com.company.consumables.finance.receivable.service.ReceivableService receivableService;

    /**
     * 功能描述: 新增批发销售单，保存为待发货状态，不扣库存
     *
     * @param vo 批发入参
     * @return 销售单主键
     * @author honghui
     * @date 2026/06/30 16:05
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createWholesaleOrder(WholesaleSaveVo vo) {
        Customer customer = customerService.getById(vo.getSCustomerId());
        if (customer == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND);
        }
        Warehouse warehouse = warehouseService.getById(vo.getSWarehouseId());
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND);
        }

        SaleOrder order = new SaleOrder();
        order.setSSaleNo(BizNoGenerator.generate(Constant.SALE_NO_PREFIX));
        order.setISaleType(SaleType.WHOLESALE.getCode());
        order.setSCustomerId(vo.getSCustomerId());
        order.setSWarehouseId(vo.getSWarehouseId());
        order.setIStatus(SaleStatus.PENDING.getCode());
        order.setITotalAmount(0);
        order.setDtShipTime(new Date());
        // 期望送达日期：未填时用默认占位日期，保证非 NULL
        order.setDExpectDelivery(vo.getDExpectDelivery() == null ? UNFILLED_DATE : vo.getDExpectDelivery());
        saleOrderMapper.insert(order);

        int totalAmount = saveItems(order.getSId(), vo.getItems());
        // 录单阶段不扣库存，仅回填总金额
        updateTotalAmountByReinsertNotNeeded(order, totalAmount);
        return order.getSId();
    }

    /**
     * 功能描述: 批发销售单发货。校验状态为待发货，对全部明细先做库存预校验，
     *           再逐条扣减库存并生成批发出库流水，最后单据转为已完成
     *
     * @param saleId 销售单主键
     * @author honghui
     * @date 2026/06/30 16:05
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ship(String saleId) {
        SaleOrder order = saleOrderMapper.selectById(saleId);
        if (order == null) {
            throw new BusinessException(ErrorCode.SALE_ORDER_NOT_FOUND);
        }
        if (order.getIStatus() == null || order.getIStatus() != SaleStatus.PENDING.getCode()) {
            throw new BusinessException(ErrorCode.SALE_ORDER_STATUS_INVALID);
        }

        List<SaleOrderItem> items = saleOrderItemMapper.selectBySaleId(saleId);
        // 全明细库存预校验，给出整单友好提示
        for (SaleOrderItem item : items) {
            int current = stockService.getQty(order.getSWarehouseId(), item.getSGoodsId());
            if (current < item.getIQtyBase()) {
                throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH,
                        "商品库存不足，缺口数量：" + (item.getIQtyBase() - current));
            }
        }
        // 逐条扣减并写出库流水
        for (SaleOrderItem item : items) {
            stockService.deductStock(order.getSWarehouseId(), item.getSGoodsId(),
                    item.getIQtyBase(), FlowType.WHOLESALE_OUT, saleId);
        }
        saleOrderMapper.updateStatusShipped(saleId, SaleStatus.COMPLETED.getCode(),
                new Date(), resolveOperator());

        // 第二期集成：批发发货后生成应收账款
        receivableService.generateFromSale(saleId, order.getSCustomerId(), order.getITotalAmount());
    }

    /**
     * 功能描述: 门店散卖。校验出库地点为门店类型，保存即出库扣减库存并生成散卖流水
     *
     * @param vo 散卖入参
     * @return 销售单主键
     * @author honghui
     * @date 2026/06/30 16:05
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createStoreSale(StoreSaleSaveVo vo) {
        Warehouse warehouse = warehouseService.getById(vo.getSWarehouseId());
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND);
        }
        // 仅门店类型地点允许散卖
        if (warehouse.getILocationType() == null
                || warehouse.getILocationType() != LocationType.STORE.getCode()) {
            throw new BusinessException(ErrorCode.SALE_LOCATION_NOT_STORE);
        }

        SaleOrder order = new SaleOrder();
        order.setSSaleNo(BizNoGenerator.generate(Constant.SALE_NO_PREFIX));
        order.setISaleType(SaleType.STORE.getCode());
        order.setSCustomerId(Constant.EMPTY);
        order.setSWarehouseId(vo.getSWarehouseId());
        order.setIStatus(SaleStatus.COMPLETED.getCode());
        order.setITotalAmount(0);
        order.setDtShipTime(new Date());
        order.setDExpectDelivery(UNFILLED_DATE);
        saleOrderMapper.insert(order);

        int totalAmount = saveItems(order.getSId(), vo.getItems());
        // 散卖保存即出库：逐条扣减并写散卖出库流水
        List<SaleOrderItem> items = saleOrderItemMapper.selectBySaleId(order.getSId());
        for (SaleOrderItem item : items) {
            stockService.deductStock(vo.getSWarehouseId(), item.getSGoodsId(),
                    item.getIQtyBase(), FlowType.STORE_OUT, order.getSId());
        }
        updateTotalAmountByReinsertNotNeeded(order, totalAmount);
        return order.getSId();
    }

    /**
     * 功能描述: 分页查询销售单
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 16:05
     */
    @Override
    public PageResult<SaleOrder> pageSale(SaleQueryVo query) {
        long total = saleOrderMapper.countPage(query);
        List<SaleOrder> list = saleOrderMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 查询销售单详情（含明细）
     *
     * @param sId 销售单主键
     * @return 详情
     * @author honghui
     * @date 2026/06/30 16:05
     */
    @Override
    public SaleDetailVo getDetail(String sId) {
        SaleOrder order = saleOrderMapper.selectById(sId);
        if (order == null) {
            throw new BusinessException(ErrorCode.SALE_ORDER_NOT_FOUND);
        }
        SaleDetailVo detail = new SaleDetailVo();
        detail.setSaleOrder(order);
        detail.setItems(saleOrderItemMapper.selectBySaleId(sId));
        return detail;
    }

    /**
     * 功能描述: 查询送货提醒（待发货批发单，按期望送达日期升序）
     *
     * @return 待发货批发单列表
     * @author honghui
     * @date 2026/07/06 12:18
     */
    @Override
    public List<SaleOrder> deliveryReminder() {
        return saleOrderMapper.selectDeliveryReminder();
    }

    /**
     * 功能描述: 保存销售明细，折算为基本单位并返回总金额（分）
     *
     * @param saleId  销售单主键
     * @param itemVos 明细入参
     * @return 总金额（分）
     * @author honghui
     * @date 2026/06/30 16:05
     */
    private int saveItems(String saleId, List<SaleItemVo> itemVos) {
        int totalAmount = 0;
        for (SaleItemVo itemVo : itemVos) {
            int rate = goodsService.resolveConvertRate(itemVo.getSGoodsId(), itemVo.getSInputUnit());
            int qtyBase = itemVo.getIInputQty() * rate;

            SaleOrderItem item = new SaleOrderItem();
            item.setSSaleId(saleId);
            item.setSGoodsId(itemVo.getSGoodsId());
            item.setIQtyBase(qtyBase);
            item.setSInputUnit(itemVo.getSInputUnit());
            item.setIInputQty(itemVo.getIInputQty());
            item.setIPrice(itemVo.getIPrice());
            // 折扣与折后价：折扣默认100（不打折），折后价=原价×折扣/100
            int discount = (itemVo.getIDiscount() != null && itemVo.getIDiscount() > 0) ? itemVo.getIDiscount() : 100;
            int discountPrice = itemVo.getIPrice() * discount / 100;
            item.setIDiscount(discount);
            item.setIDiscountPrice(discountPrice);
            item.setSRemark(itemVo.getSRemark() != null ? itemVo.getSRemark() : "");
            saleOrderItemMapper.insert(item);

            // 按折后价计算行金额
            totalAmount += discountPrice * qtyBase;
        }
        return totalAmount;
    }

    /**
     * 功能描述: 回填销售单总金额
     *
     * @param order       销售单
     * @param totalAmount 总金额
     * @author honghui
     * @date 2026/06/30 16:05
     */
    private void updateTotalAmountByReinsertNotNeeded(SaleOrder order, int totalAmount) {
        order.setITotalAmount(totalAmount);
        saleOrderMapper.updateTotalAmount(order.getSId(), totalAmount, resolveOperator());
    }

    /**
     * 功能描述: 解析当前操作人，无上下文时取默认用户
     *
     * @return 操作人
     * @author honghui
     * @date 2026/06/30 16:05
     */
    private String resolveOperator() {
        String currentUser = UserContext.getCurrentUser();
        return StringUtils.hasText(currentUser) ? currentUser : defaultUser;
    }
}
