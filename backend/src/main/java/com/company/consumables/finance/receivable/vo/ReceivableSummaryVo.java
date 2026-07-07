package com.company.consumables.finance.receivable.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 销售欠款汇总（按客户聚合）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class ReceivableSummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 客户ID */
    private String sCustomerId;

    /** 客户名称 */
    private String sCustomerName;

    /** 应收总额（分） */
    private Integer iTotalAmount;

    /** 已收总额（分） */
    private Integer iReceivedAmount;

    /** 未收总额/欠款（分） */
    private Integer iUnreceivedAmount;
}
