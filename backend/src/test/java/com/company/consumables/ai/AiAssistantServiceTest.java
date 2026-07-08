package com.company.consumables.ai;

import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentRouter;
import com.company.consumables.ai.llm.LlmClient;
import com.company.consumables.ai.llm.LlmProperties;
import com.company.consumables.ai.llm.LlmUnavailableException;
import com.company.consumables.ai.mapper.AiChatLogMapper;
import com.company.consumables.ai.service.impl.AiAssistantServiceImpl;
import com.company.consumables.ai.vo.AiAnswerVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 类描述: AI 编排服务单元测试。mock LlmClient 返回意图/拒答/异常，验证：
 *         命中意图取真实回答、reject 拒答、大模型不可用降级（Property 5、6）。不调真实大模型。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
class AiAssistantServiceTest {

    private LlmClient llmClient;
    private LlmProperties llmProperties;
    private IntentRouter intentRouter;
    private AiChatLogMapper aiChatLogMapper;
    private AiAssistantServiceImpl service;

    /**
     * 功能描述: 构造被测服务，注入 mock 依赖与一个假意图处理器
     *
     * @author honghui
     * @date 2026/07/08 13:10
     */
    @BeforeEach
    void setUp() {
        llmClient = mock(LlmClient.class);
        aiChatLogMapper = mock(AiChatLogMapper.class);

        llmProperties = new LlmProperties();
        llmProperties.setLogEnabled(false);

        IntentHandler salesHandler = new IntentHandler() {
            @Override
            public String intentCode() {
                return "SALES_AMOUNT";
            }

            @Override
            public String description() {
                return "查询销售额";
            }

            @Override
            public String paramsHint() {
                return "";
            }

            @Override
            public AiAnswerVo handle(Map<String, Object> params) {
                return AiAnswerVo.success("SALES_AMOUNT", "本月销售 100.00 元。", null);
            }
        };
        intentRouter = new IntentRouter(Collections.singletonList(salesHandler));

        service = new AiAssistantServiceImpl(llmClient, llmProperties, intentRouter, aiChatLogMapper);
    }

    /**
     * 功能描述: 大模型返回命中意图 → 得到该意图的真实回答（Property 2 取数走既有 Service）
     *
     * @author honghui
     * @date 2026/07/08 13:10
     */
    @Test
    void hitIntentReturnsRealAnswer() {
        when(llmClient.isConfigured()).thenReturn(true);
        when(llmClient.chat(anyString(), anyString()))
                .thenReturn("{\"intent\":\"SALES_AMOUNT\",\"params\":{}}");

        AiAnswerVo answer = service.ask("这个月卖了多少钱");
        assertEquals("SALES_AMOUNT", answer.getIntent());
        assertTrue(answer.getAnswer().contains("本月销售"));
        assertFalse(answer.isRejected());
        assertFalse(answer.isDegraded());
    }

    /**
     * 功能描述: 大模型返回 reject → 拒答（Property 5）
     *
     * @author honghui
     * @date 2026/07/08 13:10
     */
    @Test
    void rejectWhenModelRejects() {
        when(llmClient.isConfigured()).thenReturn(true);
        when(llmClient.chat(anyString(), anyString())).thenReturn("{\"reject\":true}");

        AiAnswerVo answer = service.ask("今天天气怎么样");
        assertTrue(answer.isRejected());
        assertTrue(answer.getAnswer().contains("可以问我"));
    }

    /**
     * 功能描述: 大模型返回清单外意图 → 拒答，不执行查询（Property 5）
     *
     * @author honghui
     * @date 2026/07/08 13:10
     */
    @Test
    void rejectWhenUnknownIntent() {
        when(llmClient.isConfigured()).thenReturn(true);
        when(llmClient.chat(anyString(), anyString()))
                .thenReturn("{\"intent\":\"NOT_EXIST\",\"params\":{}}");

        AiAnswerVo answer = service.ask("帮我下单");
        assertTrue(answer.isRejected());
    }

    /**
     * 功能描述: 大模型不可用（未配置）→ 降级（Property 6）
     *
     * @author honghui
     * @date 2026/07/08 13:10
     */
    @Test
    void degradeWhenNotConfigured() {
        when(llmClient.isConfigured()).thenReturn(false);

        AiAnswerVo answer = service.ask("这个月卖了多少钱");
        assertTrue(answer.isDegraded());
        assertTrue(answer.getAnswer().contains("暂时不可用"));
    }

    /**
     * 功能描述: 大模型调用抛异常 → 降级，不冒泡（Property 6）
     *
     * @author honghui
     * @date 2026/07/08 13:10
     */
    @Test
    void degradeWhenLlmThrows() {
        when(llmClient.isConfigured()).thenReturn(true);
        when(llmClient.chat(anyString(), anyString()))
                .thenThrow(new LlmUnavailableException("超时"));

        AiAnswerVo answer = service.ask("这个月卖了多少钱");
        assertTrue(answer.isDegraded());
    }
}
