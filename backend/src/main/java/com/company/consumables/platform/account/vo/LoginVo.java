package com.company.consumables.platform.account.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 类描述: 登录入参（字段名使用标准 camelCase，避免单字母前缀与 Jackson 序列化不兼容）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Data
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录名 */
    @NotBlank(message = "登录名不能为空")
    private String loginName;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
