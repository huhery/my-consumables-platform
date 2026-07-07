package com.company.consumables.purchase.service;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.purchase.entity.Purchase;
import com.company.consumables.purchase.vo.PurchaseDetailVo;
import com.company.consumables.purchase.vo.PurchaseQueryVo;
import com.company.consumables.purchase.vo.PurchaseSaveVo;

/**
 * 类描述: 进货（采购入库）服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface PurchaseService {

    /**
     * 功能描述: 新增进货单（保存即入库）
     *
     * @param vo 进货单入参
     * @return 进货单主键
     * @author honghui
     * @date 2026/06/30 15:00
     */
    String createPurchase(PurchaseSaveVo vo);

    /**
     * 功能描述: 分页查询进货单
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 15:00
     */
    PageResult<Purchase> pagePurchase(PurchaseQueryVo query);

    /**
     * 功能描述: 查询进货单详情（含明细）
     *
     * @param sId 进货单主键
     * @return 详情
     * @author honghui
     * @date 2026/06/30 15:00
     */
    PurchaseDetailVo getDetail(String sId);
}
