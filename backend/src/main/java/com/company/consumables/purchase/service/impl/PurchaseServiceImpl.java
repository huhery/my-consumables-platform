package com.company.consumables.purchase.service.impl;

import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.supplier.entity.Supplier;
import com.company.consumables.basedata.supplier.service.SupplierService;
import com.company.consumables.basedata.warehouse.entity.Warehouse;
import com.company.consumables.basedata.warehouse.service.WarehouseService;
import com.company.consumables.common.constant.Constant;
import com.company.consumables.common.enums.FlowType;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.util.BizNoGenerator;
import com.company.consumables.purchase.entity.Purchase;
import com.company.consumables.purchase.entity.PurchaseItem;
import com.company.consumables.purchase.mapper.PurchaseItemMapper;
import com.company.consumables.purchase.mapper.PurchaseMapper;
import com.company.consumables.purchase.service.PurchaseService;
import com.company.consumables.purchase.vo.PurchaseDetailVo;
import com.company.consumables.purchase.vo.PurchaseItemVo;
import com.company.consumables.purchase.vo.PurchaseQueryVo;
import com.company.consumables.purchase.vo.PurchaseSaveVo;
import com.company.consumables.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.company.consumables.common.context.UserContext;
import java.util.Date;
import java.util.List;

/**
 * 类描述: 进货（采购入库）服务实现。保存即入库：写主表+明细，并调用库存内核增加库存
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    /** 免登录场景默认操作人 */
    @Value("${consumables.audit.default-user:admin}")
    private String defaultUser;

    private final PurchaseMapper purchaseMapper;
    private final PurchaseItemMapper purchaseItemMapper;
    private final GoodsService goodsService;
    private final SupplierService supplierService;
    private final WarehouseService warehouseService;
    private final StockService stockService;
    private final com.company.consumables.finance.payable.service.PayableService payableService;

    /**
     * 功能描述: 新增进货单并入库。校验供应商与入库地点存在，逐条折算为基本单位、
     *           写明细、增加库存并生成采购入库流水，最后回填总金额
     *
     * @param vo 进货单入参
     * @return 进货单主键
     * @author honghui
     * @date 2026/06/30 15:08
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPurchase(PurchaseSaveVo vo) {
        Supplier supplier = supplierService.getById(vo.getSSupplierId());
        if (supplier == null) {
            throw new BusinessException(ErrorCode.SUPPLIER_NOT_FOUND);
        }
        Warehouse warehouse = warehouseService.getById(vo.getSWarehouseId());
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND);
        }

        // 先插入主表（总金额稍后回填）
        Purchase purchase = new Purchase();
        purchase.setSPurchaseNo(BizNoGenerator.generate(Constant.PURCHASE_NO_PREFIX));
        purchase.setSSupplierId(vo.getSSupplierId());
        purchase.setSWarehouseId(vo.getSWarehouseId());
        purchase.setITotalAmount(0);
        purchase.setDtPurchaseTime(new Date());
        purchaseMapper.insert(purchase);

        int totalAmount = 0;
        for (PurchaseItemVo itemVo : vo.getItems()) {
            // 按单位折算为基本单位数量
            int rate = goodsService.resolveConvertRate(itemVo.getSGoodsId(), itemVo.getSInputUnit());
            int qtyBase = itemVo.getIInputQty() * rate;

            PurchaseItem item = new PurchaseItem();
            item.setSPurchaseId(purchase.getSId());
            item.setSGoodsId(itemVo.getSGoodsId());
            item.setIQtyBase(qtyBase);
            item.setSInputUnit(itemVo.getSInputUnit());
            item.setIInputQty(itemVo.getIInputQty());
            item.setIPrice(itemVo.getIPrice());
            purchaseItemMapper.insert(item);

            // 增加库存并生成采购入库流水
            stockService.increaseStock(vo.getSWarehouseId(), itemVo.getSGoodsId(),
                    qtyBase, FlowType.PURCHASE_IN, purchase.getSId());

            // 累加总金额：进价（按基本单位）× 基本单位数量
            totalAmount += itemVo.getIPrice() * qtyBase;
        }

        // 回填总金额
        purchase.setITotalAmount(totalAmount);
        String currentUser = UserContext.getCurrentUser();
        String operator = StringUtils.hasText(currentUser) ? currentUser : defaultUser;
        purchaseMapper.updateTotalAmount(purchase.getSId(), totalAmount, operator);

        // 第二期集成：进货保存后生成应付账款
        payableService.generateFromPurchase(purchase.getSId(), vo.getSSupplierId(), totalAmount);
        return purchase.getSId();
    }

    /**
     * 功能描述: 分页查询进货单
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 15:08
     */
    @Override
    public PageResult<Purchase> pagePurchase(PurchaseQueryVo query) {
        long total = purchaseMapper.countPage(query);
        List<Purchase> list = purchaseMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 查询进货单详情（含明细）
     *
     * @param sId 进货单主键
     * @return 详情
     * @author honghui
     * @date 2026/06/30 15:08
     */
    @Override
    public PurchaseDetailVo getDetail(String sId) {
        Purchase purchase = purchaseMapper.selectById(sId);
        if (purchase == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "进货单不存在");
        }
        PurchaseDetailVo detail = new PurchaseDetailVo();
        detail.setPurchase(purchase);
        detail.setItems(purchaseItemMapper.selectByPurchaseId(sId));
        return detail;
    }
}
