package com.company.consumables.finance.expense.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 类描述: 费用支出/其他收入记账入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class ExpenseSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    @NotBlank(message = "分类不能为空")
    private String sCategoryId;

    /** 金额（分） */
    @NotNull(message = "金额不能为空")
    @Min(value = 1, message = "金额必须大于0")
    private Integer iAmount;

    /** 发生日期 */
    @NotNull(message = "发生日期不能为空")
    private Date dOccurDate;

    /** 备注 */
    private String sRemark;
}
