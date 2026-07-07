package com.company.consumables.basedata.supplier.service.impl;

import com.company.consumables.basedata.supplier.entity.Supplier;
import com.company.consumables.basedata.supplier.mapper.SupplierMapper;
import com.company.consumables.basedata.supplier.service.SupplierService;
import com.company.consumables.basedata.supplier.vo.SupplierQueryVo;
import com.company.consumables.basedata.supplier.vo.SupplierSaveVo;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述: 供应商服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;

    /**
     * 功能描述: 新增供应商
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:42
     */
    @Override
    public String createSupplier(SupplierSaveVo vo) {
        Supplier supplier = new Supplier();
        supplier.setSName(vo.getSName());
        supplier.setSContact(ValueUtil.defaultEmpty(vo.getSContact()));
        supplier.setSPhone(ValueUtil.defaultEmpty(vo.getSPhone()));
        supplierMapper.insert(supplier);
        return supplier.getSId();
    }

    /**
     * 功能描述: 修改供应商
     *
     * @param vo 入参
     * @author honghui
     * @date 2026/06/30 13:42
     */
    @Override
    public void updateSupplier(SupplierSaveVo vo) {
        Supplier existing = supplierMapper.selectById(vo.getSId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.SUPPLIER_NOT_FOUND);
        }
        existing.setSName(vo.getSName());
        existing.setSContact(ValueUtil.defaultEmpty(vo.getSContact()));
        existing.setSPhone(ValueUtil.defaultEmpty(vo.getSPhone()));
        supplierMapper.update(existing);
    }

    /**
     * 功能描述: 删除供应商，已关联进货单时拒绝
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/06/30 13:42
     */
    @Override
    public void deleteSupplier(String sId) {
        Supplier existing = supplierMapper.selectById(sId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.SUPPLIER_NOT_FOUND);
        }
        if (supplierMapper.countPurchaseBySupplierId(sId) > 0) {
            throw new BusinessException(ErrorCode.SUPPLIER_IN_USE);
        }
        supplierMapper.deleteById(sId);
    }

    /**
     * 功能描述: 分页查询供应商
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:42
     */
    @Override
    public PageResult<Supplier> pageSupplier(SupplierQueryVo query) {
        long total = supplierMapper.countPage(query);
        List<Supplier> list = supplierMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 按主键查询供应商
     *
     * @param sId 主键
     * @return 供应商实体
     * @author honghui
     * @date 2026/06/30 13:42
     */
    @Override
    public Supplier getById(String sId) {
        return supplierMapper.selectById(sId);
    }
}
