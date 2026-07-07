package com.company.consumables.finance.receivable.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 应收账款分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReceivableQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 客户ID */
    private String sCustomerId;

    /** 状态：1未收清 2已收清 */
    private Integer iStatus;
}
