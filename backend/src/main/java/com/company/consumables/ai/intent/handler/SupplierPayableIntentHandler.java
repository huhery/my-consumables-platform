package com.company.consumables.ai.intent.handler;

import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentSupport;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.company.consumables.finance.payable.service.PayableService;
import com.company.consumables.finance.payable.vo.PayableSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类描述: 供应商应付查询意图。映射 PayableService.summary，按未付金额降序展示应付情况。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Component
@RequiredArgsConstructor
public class SupplierPayableIntentHandler implements IntentHandler {

    private final PayableService payableService;

    @Override
    public String intentCode() {
        return "SUPPLIER_PAYABLE";
    }

    @Override
    public String description() {
        return "查询供应商应付情况（我还欠供应商多少钱）";
    }

    @Override
    public String paramsHint() {
        return "无";
    }

    /**
     * 功能描述: 取应付汇总，按未付降序渲染
     *
     * @param params 参数（无）
     * @return 回答
     * @author honghui
     * @date 2026/07/08 11:47
     */
    @Override
    public AiAnswerVo handle(Map<String, Object> params) {
        List<PayableSummaryVo> ranked = payableService.summary().stream()
                .filter(v -> v.getIUnpaidAmount() != null && v.getIUnpaidAmount() > 0)
                .sorted(Comparator.comparingInt(PayableSummaryVo::getIUnpaidAmount).reversed())
                .collect(Collectors.toList());

        if (ranked.isEmpty()) {
            return AiAnswerVo.success(intentCode(), "当前没有未付的供应商应付款。", ranked);
        }

        long total = ranked.stream().mapToLong(PayableSummaryVo::getIUnpaidAmount).sum();
        StringBuilder sb = new StringBuilder("供应商应付情况（未付合计 ")
                .append(IntentSupport.yuan(total)).append(" 元）：\n");
        int idx = 1;
        for (PayableSummaryVo v : ranked) {
            sb.append(idx++).append(". ").append(v.getSSupplierName())
                    .append("：应付 ").append(IntentSupport.yuan(v.getIUnpaidAmount())).append(" 元\n");
        }
        return AiAnswerVo.success(intentCode(), sb.toString().trim(), ranked);
    }
}
