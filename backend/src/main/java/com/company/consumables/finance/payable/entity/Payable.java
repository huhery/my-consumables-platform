package com.company.consumables.finance.payable.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 应付账款实体，对应表 TAB_PAYABLE
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Payable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 关联进货单ID */
    private String sPurchaseId;

    /** 供应商ID */
    private String sSupplierId;

    /** 应付金额（分） */
    private Integer iTotalAmount;

    /** 已付金额（分） */
    private Integer iPaidAmount;

    /** 状态：1未付清 2已付清 */
    private Integer iStatus;
}
