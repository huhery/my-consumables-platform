package com.company.consumables.ai.intent.handler;

import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentSupport;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类描述: 待送货提醒意图。映射 SaleService.deliveryReminder，列出待发货批发单及期望送达日期。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Component
@RequiredArgsConstructor
public class DeliveryReminderIntentHandler implements IntentHandler {

    private final SaleService saleService;

    /** 期望送达日期未填的哨兵值 */
    private static final String UNSET_DATE = "1970-01-01";

    /** 列表展示上限 */
    private static final int MAX_SHOW = 20;

    @Override
    public String intentCode() {
        return "DELIVERY_REMINDER";
    }

    @Override
    public String description() {
        return "查询待送货提醒（哪些批发单待发货、期望送达日期）";
    }

    @Override
    public String paramsHint() {
        return "无";
    }

    /**
     * 功能描述: 取待发货批发单并渲染
     *
     * @param params 参数（无）
     * @return 回答
     * @author honghui
     * @date 2026/07/08 11:52
     */
    @Override
    public AiAnswerVo handle(Map<String, Object> params) {
        List<SaleOrder> orders = saleService.deliveryReminder();
        if (orders == null || orders.isEmpty()) {
            return AiAnswerVo.success(intentCode(), "当前没有待发货的批发单。", orders);
        }

        StringBuilder sb = new StringBuilder("待发货批发单共 ").append(orders.size()).append(" 单：\n");
        int shown = 0;
        for (SaleOrder o : orders) {
            if (shown >= MAX_SHOW) {
                sb.append("……（仅显示前 ").append(MAX_SHOW).append(" 单）\n");
                break;
            }
            String deliver = expectDelivery(o.getDExpectDelivery());
            sb.append("- 单号 ").append(o.getSSaleNo())
                    .append("，金额 ").append(IntentSupport.yuan(o.getITotalAmount())).append(" 元")
                    .append("，期望送达 ").append(deliver).append("\n");
            shown++;
        }
        return AiAnswerVo.success(intentCode(), sb.toString().trim(), orders);
    }

    /**
     * 功能描述: 格式化期望送达日期，哨兵值显示为"未指定"
     *
     * @param date 期望送达日期
     * @return 展示文本
     * @author honghui
     * @date 2026/07/08 11:52
     */
    private String expectDelivery(Date date) {
        if (date == null) {
            return "未指定";
        }
        String s = IntentSupport.formatDate(date);
        return UNSET_DATE.equals(s) ? "未指定" : s;
    }
}
