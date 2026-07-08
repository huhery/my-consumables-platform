package com.company.consumables.ai.intent.handler;

import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentSupport;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.stock.entity.Stock;
import com.company.consumables.stock.service.StockService;
import com.company.consumables.stock.vo.StockQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 类描述: 库存查询意图。映射 StockService.pageStock，返回库存概览（记录数、总库存量、低库存条数）。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Component
@RequiredArgsConstructor
public class StockQueryIntentHandler implements IntentHandler {

    private final StockService stockService;

    /** 库存概览取样上限 */
    private static final int SAMPLE_SIZE = 500;

    /** 低库存阈值（基本单位） */
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Override
    public String intentCode() {
        return "STOCK_QUERY";
    }

    @Override
    public String description() {
        return "查询库存概览（库存种类数、总库存量、低库存商品数）";
    }

    @Override
    public String paramsHint() {
        return "lowStock（true/false，可选，是否只看低库存）";
    }

    /**
     * 功能描述: 取库存并汇总渲染
     *
     * @param params 参数（lowStock 可选）
     * @return 回答
     * @author honghui
     * @date 2026/07/08 11:50
     */
    @Override
    public AiAnswerVo handle(Map<String, Object> params) {
        StockQueryVo query = new StockQueryVo();
        query.setPageNum(1);
        query.setPageSize(SAMPLE_SIZE);
        PageResult<Stock> page = stockService.pageStock(query);
        List<Stock> list = page.getList();

        long totalQty = list.stream().mapToLong(s -> s.getIQty() == null ? 0 : s.getIQty()).sum();
        long lowCount = list.stream()
                .filter(s -> s.getIQty() != null && s.getIQty() < LOW_STOCK_THRESHOLD)
                .count();

        boolean lowOnly = "true".equalsIgnoreCase(String.valueOf(params == null ? "" : params.get("lowStock")));
        String answer;
        if (lowOnly) {
            answer = String.format("库存低于 %d 的商品有 %d 种（阈值可调整）。", LOW_STOCK_THRESHOLD, lowCount);
        } else {
            answer = String.format("当前共有 %d 种库存记录，总库存量 %d，其中低库存（少于 %d）商品 %d 种。",
                    page.getTotal(), totalQty, LOW_STOCK_THRESHOLD, lowCount);
        }
        // 结构化数据供前端渲染
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("total", page.getTotal());
        data.put("totalQty", totalQty);
        data.put("lowStockCount", lowCount);
        return AiAnswerVo.success(intentCode(), answer, data);
    }
}
