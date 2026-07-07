package com.company.consumables.finance.fund.mapper;

import com.company.consumables.finance.fund.entity.FundFlow;
import com.company.consumables.finance.fund.vo.FundFlowQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 资金流水 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface FundFlowMapper {

    /**
     * 功能描述: 新增资金流水
     *
     * @param fundFlow 资金流水实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/06 10:10
     */
    int insert(FundFlow fundFlow);

    /**
     * 功能描述: 分页查询资金流水
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/07/06 10:10
     */
    List<FundFlow> selectPage(FundFlowQueryVo query);

    /**
     * 功能描述: 统计满足条件的资金流水总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/07/06 10:10
     */
    long countPage(FundFlowQueryVo query);

    /**
     * 功能描述: 按方向汇总金额合计（用于资金汇总报表）
     *
     * @param direction 方向：1收入 2支出
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 金额合计（分）
     * @author honghui
     * @date 2026/07/06 10:10
     */
    Long sumAmountByDirection(@Param("direction") int direction,
                              @Param("startDate") java.util.Date startDate,
                              @Param("endDate") java.util.Date endDate);

    /**
     * 功能描述: 统计某分类被资金流水引用的数量（分类使用中校验）
     *
     * @param categoryId 分类ID
     * @return 数量
     * @author honghui
     * @date 2026/07/06 10:10
     */
    int countByCategoryId(@Param("categoryId") String categoryId);

    /**
     * 功能描述: 按费用/收入分类汇总金额（费用分类汇总报表用）
     *
     * @param flowType  流水类型（3费用支出 4其他收入）
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 分类汇总列表
     * @author honghui
     * @date 2026/07/06 12:20
     */
    List<com.company.consumables.finance.report.vo.ExpenseSummaryVo> summaryByCategory(
            @Param("flowType") int flowType,
            @Param("startDate") java.util.Date startDate,
            @Param("endDate") java.util.Date endDate);
}
