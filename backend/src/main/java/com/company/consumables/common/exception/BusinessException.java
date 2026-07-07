package com.company.consumables.common.exception;

import lombok.Getter;

/**
 * 类描述: 业务异常，携带业务错误码，由全局异常处理器统一转换为错误返回
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 业务错误码 */
    private final String errorCode;

    /** 附加的动态提示信息（可选，用于补充错误码固定消息之外的上下文） */
    private final String detail;

    /**
     * 功能描述: 构造业务异常
     *
     * @param errorCode 业务错误码
     * @author honghui
     * @date 2026/06/30 10:35
     */
    public BusinessException(String errorCode) {
        this(errorCode, null);
    }

    /**
     * 功能描述: 构造业务异常（带附加提示）
     *
     * @param errorCode 业务错误码
     * @param detail    附加提示信息
     * @author honghui
     * @date 2026/06/30 10:35
     */
    public BusinessException(String errorCode, String detail) {
        super(errorCode);
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
