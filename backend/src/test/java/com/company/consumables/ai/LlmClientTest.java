package com.company.consumables.ai;

import com.company.consumables.ai.llm.LlmClient;
import com.company.consumables.ai.llm.LlmProperties;
import com.company.consumables.ai.llm.LlmUnavailableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类描述: 大模型客户端单元测试。验证未配置时 isConfigured 为 false 且 chat 抛
 *         LlmUnavailableException（Property 6 的降级前提）。不发起真实网络请求。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
class LlmClientTest {

    /**
     * 功能描述: 总开关关闭时视为未配置
     *
     * @author honghui
     * @date 2026/07/08 13:20
     */
    @Test
    void notConfiguredWhenDisabled() {
        LlmProperties props = new LlmProperties();
        props.setEnabled(false);
        props.getLlm().setBaseUrl("http://x");
        props.getLlm().setApiKey("k");
        props.getLlm().setModel("m");

        LlmClient client = new LlmClient(props);
        assertFalse(client.isConfigured());
        assertThrows(LlmUnavailableException.class, () -> client.chat("sys", "q"));
    }

    /**
     * 功能描述: 缺少地址/密钥时视为未配置
     *
     * @author honghui
     * @date 2026/07/08 13:20
     */
    @Test
    void notConfiguredWhenMissingParams() {
        LlmProperties props = new LlmProperties();
        props.setEnabled(true);
        // baseUrl/apiKey/model 均为空
        LlmClient client = new LlmClient(props);
        assertFalse(client.isConfigured());
    }

    /**
     * 功能描述: 配置齐全时 isConfigured 为 true
     *
     * @author honghui
     * @date 2026/07/08 13:20
     */
    @Test
    void configuredWhenAllPresent() {
        LlmProperties props = new LlmProperties();
        props.setEnabled(true);
        props.getLlm().setBaseUrl("http://localhost:1234/v1");
        props.getLlm().setApiKey("sk-test");
        props.getLlm().setModel("qwen-plus");

        LlmClient client = new LlmClient(props);
        assertTrue(client.isConfigured());
    }
}
