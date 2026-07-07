package com.company.consumables.stock.mapper;

import com.company.consumables.stock.entity.Stock;
import com.company.consumables.stock.vo.StockQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 库存汇总 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface StockMapper {

    /**
     * 功能描述: 新增库存记录
     *
     * @param stock 库存实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 14:00
     */
    int insert(Stock stock);

    /**
     * 功能描述: 按商品与地点查询库存
     *
     * @param goodsId     商品ID
     * @param warehouseId 地点ID
     * @return 库存实体，可能为 null
     * @author honghui
     * @date 2026/06/30 14:00
     */
    Stock selectByGoodsAndWarehouse(@Param("goodsId") String goodsId,
                                    @Param("warehouseId") String warehouseId);

    /**
     * 功能描述: 增加库存数量（无条件，用于入库）
     *
     * @param goodsId     商品ID
     * @param warehouseId 地点ID
     * @param qty         增加数量（基本单位）
     * @param operator    更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 14:00
     */
    int increaseQty(@Param("goodsId") String goodsId,
                    @Param("warehouseId") String warehouseId,
                    @Param("qty") int qty,
                    @Param("operator") String operator);

    /**
     * 功能描述: 条件扣减库存数量（仅当库存充足时扣减，依据影响行数判断成功，防超卖）
     *
     * @param goodsId     商品ID
     * @param warehouseId 地点ID
     * @param qty         扣减数量（基本单位，正数）
     * @param operator    更新人
     * @return 影响行数（1 成功，0 库存不足）
     * @author honghui
     * @date 2026/06/30 14:00
     */
    int deductQtyIfEnough(@Param("goodsId") String goodsId,
                          @Param("warehouseId") String warehouseId,
                          @Param("qty") int qty,
                          @Param("operator") String operator);

    /**
     * 功能描述: 调整库存数量（可正可负，用于手工调整）
     *
     * @param goodsId     商品ID
     * @param warehouseId 地点ID
     * @param changeQty   变动数量（可正可负）
     * @param operator    更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 14:00
     */
    int adjustQty(@Param("goodsId") String goodsId,
                  @Param("warehouseId") String warehouseId,
                  @Param("changeQty") int changeQty,
                  @Param("operator") String operator);

    /**
     * 功能描述: 分页查询库存
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/06/30 14:00
     */
    List<Stock> selectPage(StockQueryVo query);

    /**
     * 功能描述: 统计满足条件的库存记录总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 14:00
     */
    long countPage(StockQueryVo query);
}
