package com.company.consumables.basedata.warehouse.controller;

import com.company.consumables.basedata.warehouse.entity.Warehouse;
import com.company.consumables.basedata.warehouse.service.WarehouseService;
import com.company.consumables.basedata.warehouse.vo.WarehouseQueryVo;
import com.company.consumables.basedata.warehouse.vo.WarehouseSaveVo;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类描述: 库存地点 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * 功能描述: 新增库存地点
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:20
     */
    @PostMapping
    public RestApiResultVo<String> create(@RequestBody @Valid WarehouseSaveVo vo) {
        return RestApiResultVo.ok(warehouseService.createWarehouse(vo));
    }

    /**
     * 功能描述: 修改库存地点
     *
     * @param id 主键
     * @param vo 入参
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 13:20
     */
    @PutMapping("/{id}")
    public RestApiResultVo<Void> update(@PathVariable("id") String id, @RequestBody @Valid WarehouseSaveVo vo) {
        vo.setSId(id);
        warehouseService.updateWarehouse(vo);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 删除库存地点
     *
     * @param id 主键
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 13:20
     */
    @DeleteMapping("/{id}")
    public RestApiResultVo<Void> delete(@PathVariable("id") String id) {
        warehouseService.deleteWarehouse(id);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 分页查询库存地点
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:20
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<Warehouse>> page(WarehouseQueryVo query) {
        return RestApiResultVo.ok(warehouseService.pageWarehouse(query));
    }
}
