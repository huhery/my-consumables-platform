package com.company.consumables.purchase.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 进货单分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    private String sSupplierId;

    /** 入库地点ID */
    private String sWarehouseId;

    /** 起始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;
}
