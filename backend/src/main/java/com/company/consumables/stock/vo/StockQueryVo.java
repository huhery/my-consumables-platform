package com.company.consumables.stock.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 库存分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private String sGoodsId;

    /** 库存地点ID */
    private String sWarehouseId;
}
