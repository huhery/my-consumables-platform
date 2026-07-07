package com.company.consumables.finance.payable.mapper;

import com.company.consumables.finance.payable.entity.Payable;
import com.company.consumables.finance.payable.vo.PayableQueryVo;
import com.company.consumables.finance.payable.vo.PayableSummaryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 应付账款 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface PayableMapper {

    /**
     * 功能描述: 新增应付账款
     *
     * @param payable 应付账款实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 11:00
     */
    int insert(Payable payable);

    /**
     * 功能描述: 按进货单ID查询应付
     *
     * @param purchaseId 进货单ID
     * @return 应付账款，可能为 null
     * @author honghui
     * @date 2026/07/06 11:00
     */
    Payable selectByPurchaseId(@Param("purchaseId") String purchaseId);

    /**
     * 功能描述: 更新已付金额与状态
     *
     * @param sId        主键
     * @param paidAmount 新的已付金额
     * @param status     新状态
     * @param operator   更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 11:00
     */
    int updatePaid(@Param("sId") String sId,
                   @Param("paidAmount") int paidAmount,
                   @Param("status") int status,
                   @Param("operator") String operator);

    /**
     * 功能描述: 分页查询应付
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/07/06 11:00
     */
    List<Payable> selectPage(PayableQueryVo query);

    /**
     * 功能描述: 统计满足条件的应付总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/07/06 11:00
     */
    long countPage(PayableQueryVo query);

    /**
     * 功能描述: 按供应商聚合采购付款汇总
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 11:00
     */
    List<PayableSummaryVo> summaryBySupplier();

    /**
     * 功能描述: 查询某供应商的全部应付明细
     *
     * @param supplierId 供应商ID
     * @return 应付列表
     * @author honghui
     * @date 2026/07/06 11:00
     */
    List<Payable> selectBySupplierId(@Param("supplierId") String supplierId);

    /**
     * 功能描述: 汇总全部应付未付金额（采购付款汇总报表用）
     *
     * @return 未付合计（分）
     * @author honghui
     * @date 2026/07/06 12:00
     */
    Long sumUnpaid();
}
