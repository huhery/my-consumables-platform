package com.company.consumables.finance.payable.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 采购付款汇总（按供应商聚合）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class PayableSummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    private String sSupplierId;

    /** 供应商名称 */
    private String sSupplierName;

    /** 应付总额（分） */
    private Integer iTotalAmount;

    /** 已付总额（分） */
    private Integer iPaidAmount;

    /** 未付总额（分） */
    private Integer iUnpaidAmount;
}
