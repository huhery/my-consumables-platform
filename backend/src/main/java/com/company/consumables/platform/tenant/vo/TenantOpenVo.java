package com.company.consumables.platform.tenant.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 类描述: 开通商家入参（字段名使用标准 camelCase，避免单字母前缀与 Jackson 序列化不兼容）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Data
public class TenantOpenVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商家名称 */
    @NotBlank(message = "商家名称不能为空")
    private String name;

    /** 初始登录名 */
    @NotBlank(message = "登录名不能为空")
    private String loginName;

    /** 初始密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 有效期（年），默认 1 年 */
    private Integer expireYears;

    /** 是否开通 AI 智能问数（默认 false） */
    private Boolean aiEnabled;
}
