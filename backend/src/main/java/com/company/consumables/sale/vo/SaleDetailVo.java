package com.company.consumables.sale.vo;

import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.entity.SaleOrderItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 类描述: 销售单详情（主表 + 明细）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class SaleDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 销售单主表 */
    private SaleOrder saleOrder;

    /** 销售明细列表 */
    private List<SaleOrderItem> items;
}
