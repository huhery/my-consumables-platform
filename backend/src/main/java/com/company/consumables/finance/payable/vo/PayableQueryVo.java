package com.company.consumables.finance.payable.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 应付账款分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PayableQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    private String sSupplierId;

    /** 状态：1未付清 2已付清 */
    private Integer iStatus;
}
