package com.company.consumables.stock.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 库存汇总实体（商品 × 地点），对应表 TAB_STOCK
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Stock extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private String sGoodsId;

    /** 库存地点ID */
    private String sWarehouseId;

    /** 当前库存数量（基本单位） */
    private Integer iQty;
}
