package com.company.consumables.sale.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 销售单明细实体，对应表 TAB_SALE_ORDER_ITEM
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SaleOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 销售单主表ID */
    private String sSaleId;

    /** 商品ID */
    private String sGoodsId;

    /** 折算后基本单位数量 */
    private Integer iQtyBase;

    /** 录入时单位名称 */
    private String sInputUnit;

    /** 录入时数量 */
    private Integer iInputQty;

    /** 售价（分，按基本单位计） */
    private Integer iPrice;
}
