package com.company.consumables.basedata.supplier.mapper;

import com.company.consumables.basedata.supplier.entity.Supplier;
import com.company.consumables.basedata.supplier.vo.SupplierQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 供应商 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface SupplierMapper {

    /**
     * 功能描述: 新增供应商
     *
     * @param supplier 供应商实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:38
     */
    int insert(Supplier supplier);

    /**
     * 功能描述: 更新供应商
     *
     * @param supplier 供应商实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:38
     */
    int update(Supplier supplier);

    /**
     * 功能描述: 按主键物理删除
     *
     * @param sId 主键
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:38
     */
    int deleteById(@Param("sId") String sId);

    /**
     * 功能描述: 按主键查询
     *
     * @param sId 主键
     * @return 供应商实体，可能为 null
     * @author honghui
     * @date 2026/06/30 13:38
     */
    Supplier selectById(@Param("sId") String sId);

    /**
     * 功能描述: 分页查询
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/06/30 13:38
     */
    List<Supplier> selectPage(SupplierQueryVo query);

    /**
     * 功能描述: 统计满足条件的总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 13:38
     */
    long countPage(SupplierQueryVo query);

    /**
     * 功能描述: 统计该供应商关联的进货单数（使用中校验）
     *
     * @param supplierId 供应商ID
     * @return 数量
     * @author honghui
     * @date 2026/06/30 13:38
     */
    int countPurchaseBySupplierId(@Param("supplierId") String supplierId);
}
