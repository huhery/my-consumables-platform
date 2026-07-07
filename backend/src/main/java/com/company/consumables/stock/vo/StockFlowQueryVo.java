package com.company.consumables.stock.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 出入库流水分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockFlowQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private String sGoodsId;

    /** 库存地点ID */
    private String sWarehouseId;

    /** 流水类型 */
    private Integer iFlowType;

    /** 起始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;
}
