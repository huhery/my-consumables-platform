package com.company.consumables.basedata.supplier.service;

import com.company.consumables.basedata.supplier.entity.Supplier;
import com.company.consumables.basedata.supplier.vo.SupplierQueryVo;
import com.company.consumables.basedata.supplier.vo.SupplierSaveVo;
import com.company.consumables.common.result.PageResult;

/**
 * 类描述: 供应商服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface SupplierService {

    /**
     * 功能描述: 新增供应商
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:40
     */
    String createSupplier(SupplierSaveVo vo);

    /**
     * 功能描述: 修改供应商
     *
     * @param vo 入参（含主键）
     * @author honghui
     * @date 2026/06/30 13:40
     */
    void updateSupplier(SupplierSaveVo vo);

    /**
     * 功能描述: 删除供应商（已关联进货单时拒绝）
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/06/30 13:40
     */
    void deleteSupplier(String sId);

    /**
     * 功能描述: 分页查询供应商
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:40
     */
    PageResult<Supplier> pageSupplier(SupplierQueryVo query);

    /**
     * 功能描述: 按主键查询供应商（供其他模块校验使用）
     *
     * @param sId 主键
     * @return 供应商实体，可能为 null
     * @author honghui
     * @date 2026/06/30 13:40
     */
    Supplier getById(String sId);
}
