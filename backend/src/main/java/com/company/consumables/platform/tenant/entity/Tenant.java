package com.company.consumables.platform.tenant.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 租户（商家）实体，对应表 TAB_TENANT
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Tenant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 商家名称 */
    private String sName;

    /** 状态：1启用 2停用 */
    private Integer iStatus;

    /** 开通时间 */
    private Date dtOpenTime;

    /** 到期日期（按年收费管理，到期后登录被拒） */
    private Date dExpireDate;

    /** AI 开关：0关闭 1开启（平台管理员控制，作为增值服务） */
    private Integer iAiEnabled;
}
