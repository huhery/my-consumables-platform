package com.company.consumables.basedata.warehouse.mapper;

import com.company.consumables.basedata.warehouse.entity.Warehouse;
import com.company.consumables.basedata.warehouse.vo.WarehouseQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 库存地点 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface WarehouseMapper {

    /**
     * 功能描述: 新增库存地点
     *
     * @param warehouse 库存地点实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:10
     */
    int insert(Warehouse warehouse);

    /**
     * 功能描述: 更新库存地点
     *
     * @param warehouse 库存地点实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:10
     */
    int update(Warehouse warehouse);

    /**
     * 功能描述: 按主键物理删除
     *
     * @param sId 主键
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:10
     */
    int deleteById(@Param("sId") String sId);

    /**
     * 功能描述: 按主键查询
     *
     * @param sId 主键
     * @return 库存地点实体，可能为 null
     * @author honghui
     * @date 2026/06/30 13:10
     */
    Warehouse selectById(@Param("sId") String sId);

    /**
     * 功能描述: 按编码统计数量（编码唯一校验，排除指定主键）
     *
     * @param sCode     地点编码
     * @param excludeId 需排除的主键
     * @return 数量
     * @author honghui
     * @date 2026/06/30 13:10
     */
    int countByCode(@Param("sCode") String sCode, @Param("excludeId") String excludeId);

    /**
     * 功能描述: 分页查询
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/06/30 13:10
     */
    List<Warehouse> selectPage(WarehouseQueryVo query);

    /**
     * 功能描述: 统计满足条件的总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 13:10
     */
    long countPage(WarehouseQueryVo query);

    /**
     * 功能描述: 统计该地点在库存表中的记录数（使用中校验）
     *
     * @param warehouseId 地点ID
     * @return 数量
     * @author honghui
     * @date 2026/06/30 13:10
     */
    int countStockByWarehouseId(@Param("warehouseId") String warehouseId);

    /**
     * 功能描述: 统计该地点在流水表中的记录数（使用中校验）
     *
     * @param warehouseId 地点ID
     * @return 数量
     * @author honghui
     * @date 2026/06/30 13:10
     */
    int countFlowByWarehouseId(@Param("warehouseId") String warehouseId);
}
