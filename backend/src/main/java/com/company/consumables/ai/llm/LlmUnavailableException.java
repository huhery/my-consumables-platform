package com.company.consumables.ai.llm;

/**
 * 类描述: 大模型不可用异常。未配置、超时、HTTP 错误或响应解析失败时抛出，
 *         由 AI 编排服务捕获并转为友好降级提示，绝不冒泡影响系统其他功能。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
public class LlmUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 功能描述: 构造异常
     *
     * @param message 错误信息
     * @author honghui
     * @date 2026/07/08 10:30
     */
    public LlmUnavailableException(String message) {
        super(message);
    }

    /**
     * 功能描述: 构造异常（带原因）
     *
     * @param message 错误信息
     * @param cause   原因
     * @author honghui
     * @date 2026/07/08 10:30
     */
    public LlmUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
