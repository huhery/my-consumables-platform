package com.company.consumables.ai.intent;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述: 意图路由。Spring 注入全部 IntentHandler 实现，建立 code→handler 映射，
 *         对外提供：给大模型的意图清单文本、按 code 分发、可问范围提示。
 *         "注册即生效"：新增 IntentHandler Bean 自动进入路由，无需改本类。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Component
public class IntentRouter {

    /** 意图编码 -> 处理器（保持注册顺序） */
    private final Map<String, IntentHandler> handlerMap = new LinkedHashMap<>();

    /**
     * 功能描述: 构造路由，收集所有意图处理器
     *
     * @param handlers Spring 注入的全部意图处理器
     * @author honghui
     * @date 2026/07/08 11:20
     */
    public IntentRouter(List<IntentHandler> handlers) {
        for (IntentHandler handler : handlers) {
            handlerMap.put(handler.intentCode(), handler);
        }
    }

    /**
     * 功能描述: 按意图编码获取处理器
     *
     * @param intentCode 意图编码
     * @return 处理器，不存在返回 null
     * @author honghui
     * @date 2026/07/08 11:20
     */
    public IntentHandler getHandler(String intentCode) {
        if (intentCode == null) {
            return null;
        }
        return handlerMap.get(intentCode);
    }

    /**
     * 功能描述: 是否存在该意图
     *
     * @param intentCode 意图编码
     * @return true 存在
     * @author honghui
     * @date 2026/07/08 11:20
     */
    public boolean contains(String intentCode) {
        return intentCode != null && handlerMap.containsKey(intentCode);
    }

    /**
     * 功能描述: 生成给大模型的意图清单文本（编码 + 说明 + 参数提示）
     *
     * @return 意图清单文本
     * @author honghui
     * @date 2026/07/08 11:20
     */
    public String buildIntentCatalog() {
        StringBuilder sb = new StringBuilder();
        for (IntentHandler h : handlerMap.values()) {
            sb.append("- ").append(h.intentCode()).append("：").append(h.description());
            String hint = h.paramsHint();
            if (hint != null && !hint.isEmpty()) {
                sb.append("（参数：").append(hint).append("）");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 功能描述: 生成拒答时提示的"可问范围"文本
     *
     * @return 可问范围提示
     * @author honghui
     * @date 2026/07/08 11:20
     */
    public String buildScopeHint() {
        List<String> descriptions = new ArrayList<>();
        for (IntentHandler h : handlerMap.values()) {
            descriptions.add(h.description());
        }
        return "抱歉，我暂时无法回答这个问题。您可以问我：" + String.join("、", descriptions) + "。";
    }
}
