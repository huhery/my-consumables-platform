package com.company.consumables.common.exception;

import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.error.ErrorCodeMessageHolder;
import com.company.consumables.common.result.RestApiResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 类描述: 全局异常处理器，统一捕获业务异常、参数校验异常与未预期异常
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorCodeMessageHolder errorCodeMessageHolder;

    /**
     * 功能描述: 处理业务异常，转换为错误码返回
     *
     * @param e 业务异常
     * @return 统一错误返回
     * @author honghui
     * @date 2026/06/30 10:40
     */
    @ExceptionHandler(BusinessException.class)
    public RestApiResultVo<Void> handleBusinessException(BusinessException e) {
        String message = errorCodeMessageHolder.getMessage(e.getErrorCode());
        // 若存在附加提示，拼接到固定消息后
        if (e.getDetail() != null && !e.getDetail().isEmpty()) {
            message = message + "：" + e.getDetail();
        }
        log.warn("业务异常: code={}, message={}", e.getErrorCode(), message);
        return RestApiResultVo.error(e.getErrorCode(), message);
    }

    /**
     * 功能描述: 处理 @Valid 参数校验异常（请求体）
     *
     * @param e 参数校验异常
     * @return 统一错误返回
     * @author honghui
     * @date 2026/06/30 10:40
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestApiResultVo<Void> handleValidException(MethodArgumentNotValidException e) {
        String detail = extractFirstFieldError(e.getBindingResult().getFieldError());
        String message = errorCodeMessageHolder.getMessage(ErrorCode.PARAM_INVALID) + "：" + detail;
        log.warn("参数校验失败: {}", detail);
        return RestApiResultVo.error(ErrorCode.PARAM_INVALID, message);
    }

    /**
     * 功能描述: 处理表单绑定校验异常
     *
     * @param e 绑定异常
     * @return 统一错误返回
     * @author honghui
     * @date 2026/06/30 10:40
     */
    @ExceptionHandler(BindException.class)
    public RestApiResultVo<Void> handleBindException(BindException e) {
        String detail = extractFirstFieldError(e.getBindingResult().getFieldError());
        String message = errorCodeMessageHolder.getMessage(ErrorCode.PARAM_INVALID) + "：" + detail;
        log.warn("参数绑定失败: {}", detail);
        return RestApiResultVo.error(ErrorCode.PARAM_INVALID, message);
    }

    /**
     * 功能描述: 兜底处理未预期异常，不向外暴露堆栈
     *
     * @param e 异常
     * @return 统一系统错误返回
     * @author honghui
     * @date 2026/06/30 10:40
     */
    @ExceptionHandler(Exception.class)
    public RestApiResultVo<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return RestApiResultVo.error(ErrorCode.SYSTEM_ERROR,
                errorCodeMessageHolder.getMessage(ErrorCode.SYSTEM_ERROR));
    }

    /**
     * 功能描述: 提取首个字段校验错误描述
     *
     * @param fieldError 字段错误
     * @return 错误描述
     * @author honghui
     * @date 2026/06/30 10:40
     */
    private String extractFirstFieldError(FieldError fieldError) {
        if (fieldError == null) {
            return "";
        }
        return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }
}
