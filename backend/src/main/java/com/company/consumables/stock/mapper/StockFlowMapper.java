package com.company.consumables.stock.mapper;

import com.company.consumables.stock.entity.StockFlow;
import com.company.consumables.stock.vo.StockFlowQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 出入库流水 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface StockFlowMapper {

    /**
     * 功能描述: 新增流水
     *
     * @param flow 流水实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 14:05
     */
    int insert(StockFlow flow);

    /**
     * 功能描述: 分页查询流水
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/06/30 14:05
     */
    List<StockFlow> selectPage(StockFlowQueryVo query);

    /**
     * 功能描述: 统计满足条件的流水总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 14:05
     */
    long countPage(StockFlowQueryVo query);

    /**
     * 功能描述: 汇总某商品某地点的全部流水变动数量（用于库存一致性校验）
     *
     * @param goodsId     商品ID
     * @param warehouseId 地点ID
     * @return 变动数量之和
     * @author honghui
     * @date 2026/06/30 14:05
     */
    Integer sumChangeQty(@Param("goodsId") String goodsId,
                         @Param("warehouseId") String warehouseId);
}
