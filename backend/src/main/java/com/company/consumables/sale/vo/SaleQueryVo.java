package com.company.consumables.sale.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 销售单分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SaleQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 客户ID */
    private String sCustomerId;

    /** 出库地点ID */
    private String sWarehouseId;

    /** 销售类型：1批发 2散卖 */
    private Integer iSaleType;

    /** 单据状态：1待发货 2已完成 */
    private Integer iStatus;

    /** 起始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;
}
