package com.company.consumables.purchase.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 进货单主表实体，对应表 TAB_PURCHASE
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Purchase extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 进货单号 */
    private String sPurchaseNo;

    /** 供应商ID */
    private String sSupplierId;

    /** 入库地点ID */
    private String sWarehouseId;

    /** 总金额（分） */
    private Integer iTotalAmount;

    /** 进货时间 */
    private Date dtPurchaseTime;
}
