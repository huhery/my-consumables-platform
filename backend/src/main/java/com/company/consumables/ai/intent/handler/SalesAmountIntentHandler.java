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
 * 类描述: 销售额查询意图。映射 ReportService.fundSummary 的收入部分，回答某时间段销售收款金额。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Component
@RequiredArgsConstructor
public class SalesAmountIntentHandler implements IntentHandler {

    private final ReportService reportService;

    @Override
    public String intentCode() {
        return "SALES_AMOUNT";
    }

    @Override
    public String description() {
        return "查询某时间段的销售收款金额（如本月卖了多少钱）";
    }

    @Override
    public String paramsHint() {
        return "startDate、endDate（yyyy-MM-dd，可选，缺省为本月）";
    }

    /**
     * 功能描述: 取时间段收入合计并渲染
     *
     * @param params 参数（startDate/endDate 可选）
     * @return 回答
     * @author honghui
     * @date 2026/07/08 11:42
     */
    @Override
    public AiAnswerVo handle(Map<String, Object> params) {
        Date start = IntentSupport.startDateOrMonthStart(params);
        Date end = IntentSupport.endDateOrToday(params);
        FundSummaryVo summary = reportService.fundSummary(start, end);

        String answer = String.format("%s 至 %s，销售收款合计 %s 元。",
                IntentSupport.formatDate(start), IntentSupport.formatDate(end),
                IntentSupport.yuan(summary.getIncomeTotal()));
        return AiAnswerVo.success(intentCode(), answer, summary);
    }
}
