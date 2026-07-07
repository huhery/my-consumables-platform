package com.company.consumables.finance.report.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 资金汇总（收入合计/支出合计/净额）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class FundSummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 收入合计（分） */
    private Long incomeTotal;

    /** 支出合计（分） */
    private Long expenseTotal;

    /** 净额（分） = 收入 − 支出 */
    private Long netAmount;

    /** 应收未收合计（分） */
    private Long unreceivedTotal;

    /** 应付未付合计（分） */
    private Long unpaidTotal;
}
