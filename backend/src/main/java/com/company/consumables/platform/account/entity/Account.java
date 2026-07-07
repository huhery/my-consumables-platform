package com.company.consumables.platform.account.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 账号实体，对应表 TAB_ACCOUNT。支持一租户多账号 + 角色
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 登录名（全局唯一） */
    private String sLoginName;

    /** 密码（BCrypt 密文） */
    private String sPassword;

    /** 角色：1平台管理员 2商家管理员 */
    private Integer iRole;

    /** 状态：1启用 2停用 */
    private Integer iStatus;
}
