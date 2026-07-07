package com.company.consumables.purchase.mapper;

import com.company.consumables.purchase.entity.Purchase;
import com.company.consumables.purchase.vo.PurchaseQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 进货单主表 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface PurchaseMapper {

    /**
     * 功能描述: 新增进货单
     *
     * @param purchase 进货单实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 14:50
     */
    int insert(Purchase purchase);

    /**
     * 功能描述: 更新进货单总金额
     *
     * @param sId         进货单主键
     * @param totalAmount 总金额（分）
     * @param operator    更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 15:10
     */
    int updateTotalAmount(@Param("sId") String sId,
                          @Param("totalAmount") int totalAmount,
                          @Param("operator") String operator);

    /**
     * 功能描述: 按主键查询进货单
     *
     * @param sId 主键
     * @return 进货单实体，可能为 null
     * @author honghui
     * @date 2026/06/30 14:50
     */
    Purchase selectById(@Param("sId") String sId);

    /**
     * 功能描述: 分页查询进货单
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/06/30 14:50
     */
    List<Purchase> selectPage(PurchaseQueryVo query);

    /**
     * 功能描述: 统计满足条件的进货单总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 14:50
     */
    long countPage(PurchaseQueryVo query);
}
