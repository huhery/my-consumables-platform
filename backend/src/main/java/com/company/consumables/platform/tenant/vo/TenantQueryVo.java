package com.company.consumables.platform.tenant.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 商家分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 商家名称（模糊） */
    private String sName;

    /** 状态 */
    private Integer iStatus;
}
