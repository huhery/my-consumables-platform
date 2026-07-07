package com.company.consumables.finance.receivable.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 应收账款实体，对应表 TAB_RECEIVABLE
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Receivable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 关联销售单ID */
    private String sSaleId;

    /** 客户ID */
    private String sCustomerId;

    /** 应收金额（分） */
    private Integer iTotalAmount;

    /** 已收金额（分） */
    private Integer iReceivedAmount;

    /** 状态：1未收清 2已收清 */
    private Integer iStatus;
}
