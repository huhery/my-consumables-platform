package com.company.consumables.finance.receivable.mapper;

import com.company.consumables.finance.receivable.entity.Receivable;
import com.company.consumables.finance.receivable.vo.ReceivableQueryVo;
import com.company.consumables.finance.receivable.vo.ReceivableSummaryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 应收账款 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface ReceivableMapper {

    /**
     * 功能描述: 新增应收账款
     *
     * @param receivable 应收账款实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 10:00
     */
    int insert(Receivable receivable);

    /**
     * 功能描述: 按销售单ID查询应收
     *
     * @param saleId 销售单ID
     * @return 应收账款，可能为 null
     * @author honghui
     * @date 2026/07/06 10:00
     */
    Receivable selectBySaleId(@Param("saleId") String saleId);

    /**
     * 功能描述: 更新已收金额与状态
     *
     * @param sId             主键
     * @param receivedAmount  新的已收金额
     * @param status          新状态
     * @param operator        更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 10:00
     */
    int updateReceived(@Param("sId") String sId,
                       @Param("receivedAmount") int receivedAmount,
                       @Param("status") int status,
                       @Param("operator") String operator);

    /**
     * 功能描述: 分页查询应收
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/07/06 10:00
     */
    List<Receivable> selectPage(ReceivableQueryVo query);

    /**
     * 功能描述: 统计满足条件的应收总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/07/06 10:00
     */
    long countPage(ReceivableQueryVo query);

    /**
     * 功能描述: 按客户聚合销售欠款汇总
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 10:00
     */
    List<ReceivableSummaryVo> summaryByCustomer();

    /**
     * 功能描述: 查询某客户的全部应收明细
     *
     * @param customerId 客户ID
     * @return 应收列表
     * @author honghui
     * @date 2026/07/06 10:00
     */
    List<Receivable> selectByCustomerId(@Param("customerId") String customerId);

    /**
     * 功能描述: 汇总全部应收未收金额（销售欠款汇总报表用）
     *
     * @return 未收合计（分）
     * @author honghui
     * @date 2026/07/06 12:00
     */
    Long sumUnreceived();
}
