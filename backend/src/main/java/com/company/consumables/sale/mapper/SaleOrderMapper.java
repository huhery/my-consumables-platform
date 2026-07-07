package com.company.consumables.sale.mapper;

import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.vo.SaleQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 类描述: 销售单主表 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface SaleOrderMapper {

    /**
     * 功能描述: 新增销售单
     *
     * @param order 销售单实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 15:50
     */
    int insert(SaleOrder order);

    /**
     * 功能描述: 更新销售单总金额
     *
     * @param sId         主键
     * @param totalAmount 总金额（分）
     * @param operator    更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 16:08
     */
    int updateTotalAmount(@Param("sId") String sId,
                          @Param("totalAmount") int totalAmount,
                          @Param("operator") String operator);

    /**
     * 功能描述: 按主键查询销售单
     *
     * @param sId 主键
     * @return 销售单实体，可能为 null
     * @author honghui
     * @date 2026/06/30 15:50
     */
    SaleOrder selectById(@Param("sId") String sId);

    /**
     * 功能描述: 更新销售单状态与发货时间（用于发货完成）
     *
     * @param sId      主键
     * @param status   目标状态
     * @param shipTime 发货时间
     * @param operator 更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 15:50
     */
    int updateStatusShipped(@Param("sId") String sId,
                            @Param("status") int status,
                            @Param("shipTime") Date shipTime,
                            @Param("operator") String operator);

    /**
     * 功能描述: 分页查询销售单
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/06/30 15:50
     */
    List<SaleOrder> selectPage(SaleQueryVo query);

    /**
     * 功能描述: 统计满足条件的销售单总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 15:50
     */
    long countPage(SaleQueryVo query);

    /**
     * 功能描述: 查询待发货批发销售单（送货提醒用，按期望送达日期升序）
     *
     * @return 待发货批发单列表
     * @author honghui
     * @date 2026/07/06 12:10
     */
    List<SaleOrder> selectDeliveryReminder();
}
