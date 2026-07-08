package com.company.consumables.ai.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 类描述: 大模型客户端。基于原生 RestTemplate 调用 OpenAI 兼容的对话补全接口
 *         （POST {baseUrl}/chat/completions），只负责"发送系统提示+用户问题、返回模型文本"，
 *         不接触数据库、不生成 SQL。未配置/超时/错误统一抛 LlmUnavailableException。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Slf4j
@Component
public class LlmClient {

    private final LlmProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    /**
     * 功能描述: 构造客户端，按配置的超时时间初始化 RestTemplate
     *
     * @param properties AI 配置
     * @author honghui
     * @date 2026/07/08 10:40
     */
    public LlmClient(LlmProperties properties) {
        this.properties = properties;
        int timeout = properties.getLlm().getTimeoutMillis();
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
    }

    /**
     * 功能描述: 判断大模型是否已配置可用（平台总开关开启且地址与密钥齐全）
     *
     * @return true 表示可用
     * @author honghui
     * @date 2026/07/08 10:40
     */
    public boolean isConfigured() {
        return properties.isEnabled()
                && StringUtils.hasText(properties.getLlm().getBaseUrl())
                && StringUtils.hasText(properties.getLlm().getApiKey())
                && StringUtils.hasText(properties.getLlm().getModel());
    }

    /**
     * 功能描述: 发起对话补全，返回模型输出的文本内容（期望为 JSON 字符串）
     *
     * @param systemPrompt 系统提示（意图清单 + 约束）
     * @param userQuestion 用户自然语言问题
     * @return 模型返回的文本内容
     * @author honghui
     * @date 2026/07/08 10:40
     */
    public String chat(String systemPrompt, String userQuestion) {
        if (!isConfigured()) {
            throw new LlmUnavailableException("大模型未配置：base-url/api-key/model 不完整或总开关关闭");
        }
        try {
            String url = trimTrailingSlash(properties.getLlm().getBaseUrl()) + "/chat/completions";
            String body = buildRequestBody(systemPrompt, userQuestion);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getLlm().getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(url, entity, String.class);
            return extractContent(response);
        } catch (LlmUnavailableException e) {
            throw e;
        } catch (Exception e) {
            log.warn("大模型调用失败：{}", e.getMessage());
            throw new LlmUnavailableException("大模型调用失败", e);
        }
    }

    /**
     * 功能描述: 构造 OpenAI 兼容请求体（temperature=0，要求 JSON 输出，仅发送提示与问题）
     *
     * @param systemPrompt 系统提示
     * @param userQuestion 用户问题
     * @return 请求体 JSON 字符串
     * @author honghui
     * @date 2026/07/08 10:40
     */
    private String buildRequestBody(String systemPrompt, String userQuestion) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", properties.getLlm().getModel());
            root.put("temperature", 0);

            ArrayNode messages = root.putArray("messages");
            ObjectNode sys = messages.addObject();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            ObjectNode user = messages.addObject();
            user.put("role", "user");
            user.put("content", userQuestion);

            // 要求 JSON 对象输出（OpenAI 兼容）
            ObjectNode respFormat = root.putObject("response_format");
            respFormat.put("type", "json_object");

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new LlmUnavailableException("构造大模型请求失败", e);
        }
    }

    /**
     * 功能描述: 从 OpenAI 兼容响应中提取 choices[0].message.content
     *
     * @param response 响应 JSON 字符串
     * @return 模型输出文本
     * @author honghui
     * @date 2026/07/08 10:40
     */
    private String extractContent(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.isNull()) {
                throw new LlmUnavailableException("大模型响应缺少 content 字段");
            }
            return content.asText();
        } catch (LlmUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new LlmUnavailableException("解析大模型响应失败", e);
        }
    }

    /**
     * 功能描述: 去除地址末尾斜杠
     *
     * @param url 地址
     * @return 处理后的地址
     * @author honghui
     * @date 2026/07/08 10:40
     */
    private String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
