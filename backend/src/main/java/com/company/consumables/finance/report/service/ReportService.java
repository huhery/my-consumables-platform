package com.company.consumables.finance.report.service;

import com.company.consumables.finance.report.vo.ExpenseSummaryVo;
import com.company.consumables.finance.report.vo.FundSummaryVo;

import java.util.Date;
import java.util.List;

/**
 * 类描述: 经营报表服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface ReportService {

    /**
     * 功能描述: 资金汇总（收入合计/支出合计/净额 + 应收未收/应付未付）
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 资金汇总
     * @author honghui
     * @date 2026/07/06 12:25
     */
    FundSummaryVo fundSummary(Date startDate, Date endDate);

    /**
     * 功能描述: 费用分类汇总
     *
     * @param categoryType 分类类型：1支出 2收入
     * @param startDate    起始日期
     * @param endDate      结束日期
     * @return 分类汇总列表
     * @author honghui
     * @date 2026/07/06 12:25
     */
    List<ExpenseSummaryVo> expenseSummary(int categoryType, Date startDate, Date endDate);
}
