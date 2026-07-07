package com.company.consumables.finance.receivable.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 类描述: 销售收款登记入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class ReceiptSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 客户ID */
    @NotBlank(message = "客户不能为空")
    private String sCustomerId;

    /** 收款金额（分） */
    @NotNull(message = "收款金额不能为空")
    @Min(value = 1, message = "收款金额必须大于0")
    private Integer iAmount;

    /** 收款日期 */
    @NotNull(message = "收款日期不能为空")
    private Date dOccurDate;

    /** 可选关联的销售单ID（不填则冲减客户总欠款） */
    private String sSaleId;

    /** 备注 */
    private String sRemark;
}
