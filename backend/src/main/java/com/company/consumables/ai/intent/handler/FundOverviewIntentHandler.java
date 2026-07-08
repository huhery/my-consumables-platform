package com.company.consumables.ai.intent.handler;

import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentSupport;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.company.consumables.finance.report.service.ReportService;
import com.company.consumables.finance.report.vo.FundSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 类描述: 收支概览意图。映射 ReportService.fundSummary，返回收入/支出/净额及应收未收、应付未付。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Component
@RequiredArgsConstructor
public class FundOverviewIntentHandler implements IntentHandler {

    private final ReportService reportService;

    @Override
    public String intentCode() {
        return "FUND_OVERVIEW";
    }

    @Override
    public String description() {
        return "查询收支概览（收入合计、支出合计、净额、应收未收、应付未付）";
    }

    @Override
    public String paramsHint() {
        return "startDate、endDate（yyyy-MM-dd，可选，缺省为本月）";
    }

    /**
     * 功能描述: 取资金汇总并渲染为自然语言
     *
     * @param params 参数（startDate/endDate 可选）
     * @return 回答
     * @author honghui
     * @date 2026/07/08 11:40
     */
    @Override
    public AiAnswerVo handle(Map<String, Object> params) {
        Date start = IntentSupport.startDateOrMonthStart(params);
        Date end = IntentSupport.endDateOrToday(params);
        FundSummaryVo summary = reportService.fundSummary(start, end);

        String answer = String.format(
                "%s 至 %s：收入合计 %s 元，支出合计 %s 元，净额 %s 元；应收未收 %s 元，应付未付 %s 元。",
                IntentSupport.formatDate(start), IntentSupport.formatDate(end),
                IntentSupport.yuan(summary.getIncomeTotal()),
                IntentSupport.yuan(summary.getExpenseTotal()),
                IntentSupport.yuan(summary.getNetAmount()),
                IntentSupport.yuan(summary.getUnreceivedTotal()),
                IntentSupport.yuan(summary.getUnpaidTotal()));
        return AiAnswerVo.success(intentCode(), answer, summary);
    }
}
