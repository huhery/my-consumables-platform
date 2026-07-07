package com.company.consumables.sale.mapper;

import com.company.consumables.sale.entity.SaleOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 销售单明细 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface SaleOrderItemMapper {

    /**
     * 功能描述: 新增销售明细
     *
     * @param item 明细实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 15:52
     */
    int insert(SaleOrderItem item);

    /**
     * 功能描述: 按销售单ID查询明细
     *
     * @param saleId 销售单主表ID
     * @return 明细列表
     * @author honghui
     * @date 2026/06/30 15:52
     */
    List<SaleOrderItem> selectBySaleId(@Param("saleId") String saleId);
}
