package com.company.consumables.stock.controller;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.stock.entity.Stock;
import com.company.consumables.stock.entity.StockFlow;
import com.company.consumables.stock.service.StockService;
import com.company.consumables.stock.vo.StockAdjustVo;
import com.company.consumables.stock.vo.StockFlowQueryVo;
import com.company.consumables.stock.vo.StockQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类描述: 库存查询与调整 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 功能描述: 分页查询库存
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 14:25
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<Stock>> page(StockQueryVo query) {
        return RestApiResultVo.ok(stockService.pageStock(query));
    }

    /**
     * 功能描述: 分页查询出入库流水
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 14:25
     */
    @GetMapping("/flow/page")
    public RestApiResultVo<PageResult<StockFlow>> flowPage(StockFlowQueryVo query) {
        return RestApiResultVo.ok(stockService.pageFlow(query));
    }

    /**
     * 功能描述: 手工库存调整
     *
     * @param vo 调整入参
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 14:25
     */
    @PostMapping("/adjust")
    public RestApiResultVo<Void> adjust(@RequestBody @Valid StockAdjustVo vo) {
        stockService.adjustStock(vo);
        return RestApiResultVo.ok();
    }
}
