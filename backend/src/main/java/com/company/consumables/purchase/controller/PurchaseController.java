package com.company.consumables.purchase.controller;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.purchase.entity.Purchase;
import com.company.consumables.purchase.service.PurchaseService;
import com.company.consumables.purchase.vo.PurchaseDetailVo;
import com.company.consumables.purchase.vo.PurchaseQueryVo;
import com.company.consumables.purchase.vo.PurchaseSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类描述: 进货（采购入库）REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * 功能描述: 新增进货单（保存即入库）
     *
     * @param vo 进货单入参
     * @return 进货单主键
     * @author honghui
     * @date 2026/06/30 15:15
     */
    @PostMapping
    public RestApiResultVo<String> create(@RequestBody @Valid PurchaseSaveVo vo) {
        return RestApiResultVo.ok(purchaseService.createPurchase(vo));
    }

    /**
     * 功能描述: 分页查询进货单
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 15:15
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<Purchase>> page(PurchaseQueryVo query) {
        return RestApiResultVo.ok(purchaseService.pagePurchase(query));
    }

    /**
     * 功能描述: 查询进货单详情（含明细）
     *
     * @param id 进货单主键
     * @return 详情
     * @author honghui
     * @date 2026/06/30 15:15
     */
    @GetMapping("/{id}")
    public RestApiResultVo<PurchaseDetailVo> detail(@PathVariable("id") String id) {
        return RestApiResultVo.ok(purchaseService.getDetail(id));
    }
}
