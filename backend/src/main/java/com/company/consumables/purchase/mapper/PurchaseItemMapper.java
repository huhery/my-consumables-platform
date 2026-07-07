package com.company.consumables.purchase.mapper;

import com.company.consumables.purchase.entity.PurchaseItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 进货单明细 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface PurchaseItemMapper {

    /**
     * 功能描述: 新增进货明细
     *
     * @param item 明细实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 14:52
     */
    int insert(PurchaseItem item);

    /**
     * 功能描述: 按进货单ID查询明细
     *
     * @param purchaseId 进货单主表ID
     * @return 明细列表
     * @author honghui
     * @date 2026/06/30 14:52
     */
    List<PurchaseItem> selectByPurchaseId(@Param("purchaseId") String purchaseId);
}
