package com.company.consumables.purchase.vo;

import com.company.consumables.purchase.entity.Purchase;
import com.company.consumables.purchase.entity.PurchaseItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 类描述: 进货单详情（主表 + 明细）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class PurchaseDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 进货单主表 */
    private Purchase purchase;

    /** 进货明细列表 */
    private List<PurchaseItem> items;
}
