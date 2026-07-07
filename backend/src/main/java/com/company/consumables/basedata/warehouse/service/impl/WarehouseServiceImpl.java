package com.company.consumables.basedata.warehouse.service.impl;

import com.company.consumables.basedata.warehouse.entity.Warehouse;
import com.company.consumables.basedata.warehouse.mapper.WarehouseMapper;
import com.company.consumables.basedata.warehouse.service.WarehouseService;
import com.company.consumables.basedata.warehouse.vo.WarehouseQueryVo;
import com.company.consumables.basedata.warehouse.vo.WarehouseSaveVo;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述: 库存地点服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    /** 启用状态 */
    private static final int STATUS_ENABLED = 1;

    private final WarehouseMapper warehouseMapper;

    /**
     * 功能描述: 新增库存地点，校验编码唯一
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:18
     */
    @Override
    public String createWarehouse(WarehouseSaveVo vo) {
        if (warehouseMapper.countByCode(vo.getSCode(), null) > 0) {
            throw new BusinessException(ErrorCode.WAREHOUSE_CODE_DUPLICATE);
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setSCode(vo.getSCode());
        warehouse.setSName(vo.getSName());
        warehouse.setILocationType(vo.getILocationType());
        warehouse.setIStatus(STATUS_ENABLED);
        warehouseMapper.insert(warehouse);
        return warehouse.getSId();
    }

    /**
     * 功能描述: 修改库存地点，校验存在与编码唯一
     *
     * @param vo 入参
     * @author honghui
     * @date 2026/06/30 13:18
     */
    @Override
    public void updateWarehouse(WarehouseSaveVo vo) {
        Warehouse existing = warehouseMapper.selectById(vo.getSId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND);
        }
        if (warehouseMapper.countByCode(vo.getSCode(), vo.getSId()) > 0) {
            throw new BusinessException(ErrorCode.WAREHOUSE_CODE_DUPLICATE);
        }
        existing.setSCode(vo.getSCode());
        existing.setSName(vo.getSName());
        existing.setILocationType(vo.getILocationType());
        warehouseMapper.update(existing);
    }

    /**
     * 功能描述: 删除库存地点，已被库存或流水使用时拒绝
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/06/30 13:18
     */
    @Override
    public void deleteWarehouse(String sId) {
        Warehouse existing = warehouseMapper.selectById(sId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND);
        }
        boolean inUse = warehouseMapper.countStockByWarehouseId(sId) > 0
                || warehouseMapper.countFlowByWarehouseId(sId) > 0;
        if (inUse) {
            throw new BusinessException(ErrorCode.WAREHOUSE_IN_USE);
        }
        warehouseMapper.deleteById(sId);
    }

    /**
     * 功能描述: 分页查询库存地点
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:18
     */
    @Override
    public PageResult<Warehouse> pageWarehouse(WarehouseQueryVo query) {
        long total = warehouseMapper.countPage(query);
        List<Warehouse> list = warehouseMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 按主键查询库存地点
     *
     * @param sId 主键
     * @return 库存地点实体
     * @author honghui
     * @date 2026/06/30 13:18
     */
    @Override
    public Warehouse getById(String sId) {
        return warehouseMapper.selectById(sId);
    }
}
