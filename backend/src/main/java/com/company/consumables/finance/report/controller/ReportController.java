package com.company.consumables.finance.report.controller;

import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.finance.report.service.ReportService;
import com.company.consumables.finance.report.vo.ExpenseSummaryVo;
import com.company.consumables.finance.report.vo.FundSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 类描述: 经营报表 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 功能描述: 资金汇总报表
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 资金汇总
     * @author honghui
     * @date 2026/07/06 12:32
     */
    @GetMapping("/fund-summary")
    public RestApiResultVo<FundSummaryVo> fundSummary(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return RestApiResultVo.ok(reportService.fundSummary(startDate, endDate));
    }

    /**
     * 功能描述: 费用/收入分类汇总报表
     *
     * @param categoryType 分类类型：1支出 2收入
     * @param startDate    起始日期
     * @param endDate      结束日期
     * @return 分类汇总列表
     * @author honghui
     * @date 2026/07/06 12:32
     */
    @GetMapping("/expense-summary")
    public RestApiResultVo<List<ExpenseSummaryVo>> expenseSummary(
            @RequestParam("categoryType") int categoryType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return RestApiResultVo.ok(reportService.expenseSummary(categoryType, startDate, endDate));
    }
}
