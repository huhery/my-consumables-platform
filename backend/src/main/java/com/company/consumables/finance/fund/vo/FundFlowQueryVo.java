package com.company.consumables.finance.fund.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 资金流水分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FundFlowQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 流水类型 */
    private Integer iFlowType;

    /** 起始日期 */
    private Date startDate;

    /** 结束日期 */
    private Date endDate;
}
