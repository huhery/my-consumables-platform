package com.company.consumables.platform.account.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 登录返回结果
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Data
public class LoginResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** JWT 令牌 */
    private String token;

    /** 登录名 */
    private String loginName;

    /** 角色：1平台管理员 2商家管理员 */
    private Integer role;

    /** 租户ID（平台管理员为空） */
    private String tenantId;
}
