package com.company.consumables.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 统一 REST 接口返回结构
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class RestApiResultVo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功状态码 */
    public static final String CODE_SUCCESS = "0";

    /** 返回码：成功为 0，失败为业务错误码 */
    private String code;

    /** 提示信息 */
    private String message;

    /** 返回数据 */
    private T data;

    /**
     * 功能描述: 构造成功返回（无数据）
     *
     * @param <T> 数据类型
     * @return 成功结果
     * @author honghui
     * @date 2026/06/30 10:10
     */
    public static <T> RestApiResultVo<T> ok() {
        return ok(null);
    }

    /**
     * 功能描述: 构造成功返回（带数据）
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return 成功结果
     * @author honghui
     * @date 2026/06/30 10:10
     */
    public static <T> RestApiResultVo<T> ok(T data) {
        RestApiResultVo<T> result = new RestApiResultVo<>();
        result.setCode(CODE_SUCCESS);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 功能描述: 构造失败返回（业务错误码 + 错误信息）
     *
     * @param code    业务错误码
     * @param message 错误信息（错误码对应的中文）
     * @param <T>     数据类型
     * @return 失败结果
     * @author honghui
     * @date 2026/06/30 10:10
     */
    public static <T> RestApiResultVo<T> error(String code, String message) {
        RestApiResultVo<T> result = new RestApiResultVo<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    /**
     * 功能描述: 判断当前结果是否成功
     *
     * @return true 表示成功
     * @author honghui
     * @date 2026/06/30 10:10
     */
    public boolean isSuccess() {
        return CODE_SUCCESS.equals(this.code);
    }
}
