package com.company.consumables.finance.report.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 费用/收入分类汇总项
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class ExpenseSummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private String sCategoryId;

    /** 分类名称 */
    private String sCategoryName;

    /** 合计金额（分） */
    private Integer iAmount;
}
