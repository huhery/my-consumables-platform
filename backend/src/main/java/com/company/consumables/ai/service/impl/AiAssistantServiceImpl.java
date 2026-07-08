package com.company.consumables.ai.service.impl;

import com.company.consumables.ai.entity.AiChatLog;
import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentRouter;
import com.company.consumables.ai.llm.LlmClient;
import com.company.consumables.ai.llm.LlmProperties;
import com.company.consumables.ai.llm.LlmUnavailableException;
import com.company.consumables.ai.mapper.AiChatLogMapper;
import com.company.consumables.ai.service.AiAssistantService;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 类描述: AI 智能问数编排服务实现。核心安全约束：大模型只输出"意图编码+参数"JSON，
 *         系统据此调用既有 Service 取数（自动经租户隔离），绝不执行模型产出的 SQL；
 *         映射不到即拒答、大模型不可用即降级。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistantServiceImpl implements AiAssistantService {

    private final LlmClient llmClient;
    private final LlmProperties llmProperties;
    private final IntentRouter intentRouter;
    private final AiChatLogMapper aiChatLogMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 降级提示文案 */
    private static final String DEGRADE_MSG = "智能助手暂时不可用，请稍后再试，或使用菜单查询。";

    /**
     * 功能描述: 处理问数请求
     *
     * @param question 用户问题
     * @return 回答
     * @author honghui
     * @date 2026/07/08 12:15
     */
    @Override
    public AiAnswerVo ask(String question) {
        // 大模型未配置或总开关关闭 → 降级
        if (!llmClient.isConfigured()) {
            return logAndReturn(question, AiAnswerVo.degrade(DEGRADE_MSG));
        }
        try {
            String systemPrompt = buildSystemPrompt();
            String content = llmClient.chat(systemPrompt, question);
            AiAnswerVo answer = dispatch(content);
            return logAndReturn(question, answer);
        } catch (LlmUnavailableException e) {
            log.warn("AI 问数降级：{}", e.getMessage());
            return logAndReturn(question, AiAnswerVo.degrade(DEGRADE_MSG));
        } catch (Exception e) {
            // 任何未预期异常都降级，绝不影响系统其他功能
            log.warn("AI 问数异常降级：{}", e.getMessage(), e);
            return logAndReturn(question, AiAnswerVo.degrade(DEGRADE_MSG));
        }
    }

    /**
     * 功能描述: 解析大模型返回的 JSON，路由到意图处理器；reject/未知意图/解析失败一律拒答
     *
     * @param content 大模型返回文本（期望 JSON）
     * @return 回答
     * @author honghui
     * @date 2026/07/08 12:15
     */
    private AiAnswerVo dispatch(String content) {
        JsonNode root;
        try {
            root = objectMapper.readTree(content);
        } catch (Exception e) {
            log.warn("大模型返回非 JSON，拒答：{}", content);
            return AiAnswerVo.reject(intentRouter.buildScopeHint());
        }

        // 明确拒答
        if (root.path("reject").asBoolean(false)) {
            return AiAnswerVo.reject(intentRouter.buildScopeHint());
        }

        String intent = root.path("intent").asText("");
        IntentHandler handler = intentRouter.getHandler(intent);
        if (handler == null) {
            // 意图为空或不在预设集合 → 拒答，不执行任何查询
            return AiAnswerVo.reject(intentRouter.buildScopeHint());
        }

        Map<String, Object> params = extractParams(root.path("params"));
        return handler.handle(params);
    }

    /**
     * 功能描述: 从 params 节点提取参数为 Map（仅取标量值）
     *
     * @param paramsNode params JSON 节点
     * @return 参数 Map
     * @author honghui
     * @date 2026/07/08 12:15
     */
    private Map<String, Object> extractParams(JsonNode paramsNode) {
        Map<String, Object> params = new HashMap<>();
        if (paramsNode != null && paramsNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = paramsNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode value = entry.getValue();
                if (value.isValueNode()) {
                    params.put(entry.getKey(), value.asText());
                }
            }
        }
        return params;
    }

    /**
     * 功能描述: 构造系统提示：意图清单 + 严格约束（只选意图、只输出 JSON、不造数据、不出 SQL）
     *
     * @return 系统提示
     * @author honghui
     * @date 2026/07/08 12:15
     */
    private String buildSystemPrompt() {
        return "你是一个只做\"意图识别\"的助手。用户会用中文询问经营数据。\n"
                + "你只能从下面的意图清单中选择一个最匹配的意图，并抽取参数；不得编造数据，不得输出 SQL，不得选择清单以外的意图。\n"
                + "意图清单：\n"
                + intentRouter.buildIntentCatalog()
                + "\n输出要求：只输出一个 JSON 对象，不要任何多余文字。\n"
                + "命中意图时输出：{\"intent\":\"意图编码\",\"params\":{参数键值}}。\n"
                + "无法匹配任何意图时输出：{\"reject\":true}。\n"
                + "日期参数统一格式 yyyy-MM-dd。";
    }

    /**
     * 功能描述: 按配置可选记录问答日志，并返回回答
     *
     * @param question 用户问题
     * @param answer   回答
     * @return 回答（原样返回）
     * @author honghui
     * @date 2026/07/08 12:15
     */
    private AiAnswerVo logAndReturn(String question, AiAnswerVo answer) {
        if (llmProperties.isLogEnabled()) {
            try {
                AiChatLog logEntity = new AiChatLog();
                logEntity.setSQuestion(question);
                logEntity.setSIntent(answer.getIntent() == null ? "" : answer.getIntent());
                logEntity.setISuccess(!answer.isRejected() && !answer.isDegraded() ? 1 : 0);
                logEntity.setIDegraded(answer.isDegraded() ? 1 : 0);
                aiChatLogMapper.insert(logEntity);
            } catch (Exception e) {
                // 记日志失败不影响问答主流程
                log.warn("记录 AI 问答日志失败：{}", e.getMessage());
            }
        }
        return answer;
    }
}
