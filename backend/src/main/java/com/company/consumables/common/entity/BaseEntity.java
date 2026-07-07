package com.company.consumables.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 类描述: 实体基类，承载主键与审计字段，由 MyBatis 拦截器自动填充
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（UUID） */
    private String sId;

    /** 租户ID（多租户隔离，由框架层自动填充） */
    private String sTenantId;

    /** 创建时间 */
    private Date dtCreateTime;

    /** 更新时间 */
    private Date dtUpdateTime;

    /** 创建人 */
    private String sCreateUser;

    /** 更新人 */
    private String sUpdateUser;
}
