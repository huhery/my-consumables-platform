package com.company.consumables.finance.payable.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 类描述: 采购付款登记入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class PaymentSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    @NotBlank(message = "供应商不能为空")
    private String sSupplierId;

    /** 付款金额（分） */
    @NotNull(message = "付款金额不能为空")
    @Min(value = 1, message = "付款金额必须大于0")
    private Integer iAmount;

    /** 付款日期 */
    @NotNull(message = "付款日期不能为空")
    private Date dOccurDate;

    /** 可选关联的进货单ID（不填则冲减供应商总应付） */
    private String sPurchaseId;

    /** 备注 */
    private String sRemark;
}
