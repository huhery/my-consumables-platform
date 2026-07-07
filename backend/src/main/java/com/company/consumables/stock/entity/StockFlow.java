package com.company.consumables.stock.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 出入库流水实体，对应表 TAB_STOCK_FLOW
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockFlow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private String sGoodsId;

    /** 库存地点ID */
    private String sWarehouseId;

    /** 流水类型：1采购入库 2批发出库 3门店散卖出库 4手工调整 */
    private Integer iFlowType;

    /** 变动数量（基本单位，入库为正出库为负） */
    private Integer iChangeQty;

    /** 关联单据ID */
    private String sSourceNo;

    /** 备注 */
    private String sRemark;
}
