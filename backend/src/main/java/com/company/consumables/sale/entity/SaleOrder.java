package com.company.consumables.sale.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 销售单主表实体，对应表 TAB_SALE_ORDER
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SaleOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 销售单号 */
    private String sSaleNo;

    /** 销售类型：1批发出货 2门店散卖 */
    private Integer iSaleType;

    /** 客户ID（散卖为空） */
    private String sCustomerId;

    /** 出库地点ID */
    private String sWarehouseId;

    /** 单据状态：1待发货 2已完成 */
    private Integer iStatus;

    /** 总金额（分） */
    private Integer iTotalAmount;

    /** 发货时间 */
    private Date dtShipTime;

    /** 期望送达日期（第二期，1970-01-01 视为未填） */
    private Date dExpectDelivery;

    /** 物流状态：1未发货 2已发货 */
    private Integer iDeliverStatus;

    /** 快递公司 */
    private String sExpressCompany;

    /** 快递/物流单号 */
    private String sExpressNo;
}
