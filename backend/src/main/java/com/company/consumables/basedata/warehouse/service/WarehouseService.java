package com.company.consumables.basedata.warehouse.service;

import com.company.consumables.basedata.warehouse.entity.Warehouse;
import com.company.consumables.basedata.warehouse.vo.WarehouseQueryVo;
import com.company.consumables.basedata.warehouse.vo.WarehouseSaveVo;
import com.company.consumables.common.result.PageResult;

/**
 * 类描述: 库存地点服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface WarehouseService {

    /**
     * 功能描述: 新增库存地点
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:15
     */
    String createWarehouse(WarehouseSaveVo vo);

    /**
     * 功能描述: 修改库存地点
     *
     * @param vo 入参（含主键）
     * @author honghui
     * @date 2026/06/30 13:15
     */
    void updateWarehouse(WarehouseSaveVo vo);

    /**
     * 功能描述: 删除库存地点（已被库存或流水使用时拒绝）
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/06/30 13:15
     */
    void deleteWarehouse(String sId);

    /**
     * 功能描述: 分页查询库存地点
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:15
     */
    PageResult<Warehouse> pageWarehouse(WarehouseQueryVo query);

    /**
     * 功能描述: 按主键查询库存地点（供其他模块校验使用）
     *
     * @param sId 主键
     * @return 库存地点实体，可能为 null
     * @author honghui
     * @date 2026/06/30 13:15
     */
    Warehouse getById(String sId);
}
