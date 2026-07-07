package com.company.consumables.finance.fund.controller;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.finance.fund.entity.FundFlow;
import com.company.consumables.finance.fund.service.FundFlowService;
import com.company.consumables.finance.fund.vo.FundFlowQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类描述: 资金流水 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/api/fund-flow")
@RequiredArgsConstructor
public class FundFlowController {

    private final FundFlowService fundFlowService;

    /**
     * 功能描述: 分页查询资金流水
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 11:55
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<FundFlow>> page(FundFlowQueryVo query) {
        return RestApiResultVo.ok(fundFlowService.pageFlow(query));
    }
}
