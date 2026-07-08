package com.company.consumables.ai.intent.handler;

import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentSupport;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.company.consumables.finance.receivable.service.ReceivableService;
import com.company.consumables.finance.receivable.vo.ReceivableSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类描述: 客户欠款排行意图。映射 ReceivableService.summary，按未收金额降序取前 N 名。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Component
@RequiredArgsConstructor
public class CustomerDebtRankIntentHandler implements IntentHandler {

    private final ReceivableService receivableService;

    /** 默认取前 10 名 */
    private static final int DEFAULT_TOP_N = 10;

    @Override
    public String intentCode() {
        return "CUSTOMER_DEBT_RANK";
    }

    @Override
    public String description() {
        return "查询客户欠款排行（哪些客户欠款最多）";
    }

    @Override
    public String paramsHint() {
        return "topN（整数，可选，缺省 10）";
    }

    /**
     * 功能描述: 取欠款汇总，按未收降序取前 N 并渲染
     *
     * @param params 参数（topN 可选）
     * @return 回答
     * @author honghui
     * @date 2026/07/08 11:45
     */
    @Override
    public AiAnswerVo handle(Map<String, Object> params) {
        int topN = IntentSupport.intOrDefault(params, "topN", DEFAULT_TOP_N);
        if (topN <= 0) {
            topN = DEFAULT_TOP_N;
        }
        List<ReceivableSummaryVo> all = receivableService.summary();
        List<ReceivableSummaryVo> ranked = all.stream()
                .filter(v -> v.getIUnreceivedAmount() != null && v.getIUnreceivedAmount() > 0)
                .sorted(Comparator.comparingInt(ReceivableSummaryVo::getIUnreceivedAmount).reversed())
                .limit(topN)
                .collect(Collectors.toList());

        if (ranked.isEmpty()) {
            return AiAnswerVo.success(intentCode(), "当前没有客户欠款。", ranked);
        }

        StringBuilder sb = new StringBuilder("客户欠款排行（前 ").append(ranked.size()).append(" 名）：\n");
        int idx = 1;
        for (ReceivableSummaryVo v : ranked) {
            sb.append(idx++).append(". ").append(v.getSCustomerName())
                    .append("：欠款 ").append(IntentSupport.yuan(v.getIUnreceivedAmount())).append(" 元\n");
        }
        return AiAnswerVo.success(intentCode(), sb.toString().trim(), ranked);
    }
}
