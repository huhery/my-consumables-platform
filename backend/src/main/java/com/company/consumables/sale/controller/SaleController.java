package com.company.consumables.sale.controller;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.service.SaleService;
import com.company.consumables.sale.vo.SaleDetailVo;
import com.company.consumables.sale.vo.SaleQueryVo;
import com.company.consumables.sale.vo.StoreSaleSaveVo;
import com.company.consumables.sale.vo.WholesaleSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类描述: 出货（批发 + 门店散卖）REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    /**
     * 功能描述: 新增批发销售单（待发货）
     *
     * @param vo 批发入参
     * @return 销售单主键
     * @author honghui
     * @date 2026/06/30 16:12
     */
    @PostMapping("/wholesale")
    public RestApiResultVo<String> createWholesale(@RequestBody @Valid WholesaleSaveVo vo) {
        return RestApiResultVo.ok(saleService.createWholesaleOrder(vo));
    }

    /**
     * 功能描述: 批发销售单发货（扣库存）
     *
     * @param id 销售单主键
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 16:12
     */
    @PostMapping("/{id}/ship")
    public RestApiResultVo<Void> ship(@PathVariable("id") String id) {
        saleService.ship(id);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 门店散卖（保存即出库）
     *
     * @param vo 散卖入参
     * @return 销售单主键
     * @author honghui
     * @date 2026/06/30 16:12
     */
    @PostMapping("/store")
    public RestApiResultVo<String> createStoreSale(@RequestBody @Valid StoreSaleSaveVo vo) {
        return RestApiResultVo.ok(saleService.createStoreSale(vo));
    }

    /**
     * 功能描述: 分页查询销售单
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 16:12
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<SaleOrder>> page(SaleQueryVo query) {
        return RestApiResultVo.ok(saleService.pageSale(query));
    }

    /**
     * 功能描述: 查询销售单详情（含明细）
     *
     * @param id 销售单主键
     * @return 详情
     * @author honghui
     * @date 2026/06/30 16:12
     */
    @GetMapping("/{id}")
    public RestApiResultVo<SaleDetailVo> detail(@PathVariable("id") String id) {
        return RestApiResultVo.ok(saleService.getDetail(id));
    }

    /**
     * 功能描述: 确认发货（填入快递公司和单号）
     *
     * @param id             销售单ID
     * @param expressCompany 快递公司
     * @param expressNo      快递单号
     * @return 空结果
     * @author honghui
     * @date 2026/07/15 23:05
     */
    @PostMapping("/{id}/deliver")
    public RestApiResultVo<Void> confirmDeliver(@PathVariable("id") String id,
                                                @RequestParam(value = "expressCompany", defaultValue = "") String expressCompany,
                                                @RequestParam(value = "expressNo", defaultValue = "") String expressNo) {
        saleService.confirmDeliver(id, expressCompany, expressNo);
        return RestApiResultVo.ok();
    }
}
