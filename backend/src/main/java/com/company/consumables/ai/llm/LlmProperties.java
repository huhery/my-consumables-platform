package com.company.consumables.ai.llm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类描述: AI 智能问数配置。绑定 consumables.ai.*，包含平台总开关、日志开关与大模型接入参数。
 *         大模型采用 OpenAI 兼容协议，换厂商仅改配置无需改代码；密钥经环境变量注入，禁止硬编码。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Data
@Component
@ConfigurationProperties(prefix = "consumables.ai")
public class LlmProperties {

    /** 平台 AI 总开关：控制大模型能力是否可用 */
    private boolean enabled = false;

    /** 是否记录问答日志 */
    private boolean logEnabled = false;

    /** 大模型接入参数 */
    private Llm llm = new Llm();

    /**
     * 类描述: 大模型接入参数（OpenAI 兼容协议）
     *
     * @author honghui
     * @version 1.0
     * @date 2026/07/08
     */
    @Data
    public static class Llm {

        /** OpenAI 兼容 API 基础地址，如 https://dashscope.aliyuncs.com/compatible-mode/v1 */
        private String baseUrl = "";

        /** API 密钥（环境变量注入，禁止硬编码） */
        private String apiKey = "";

        /** 模型名称，如 qwen-plus / deepseek-chat / gpt-4o-mini */
        private String model = "";

        /** 调用超时（毫秒） */
        private int timeoutMillis = 15000;
    }
}
