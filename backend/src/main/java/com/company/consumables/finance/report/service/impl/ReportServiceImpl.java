package com.company.consumables.finance.report.service.impl;

import com.company.consumables.common.enums.CategoryType;
import com.company.consumables.common.enums.FundDirection;
import com.company.consumables.common.enums.FundFlowType;
import com.company.consumables.finance.fund.mapper.FundFlowMapper;
import com.company.consumables.finance.payable.mapper.PayableMapper;
import com.company.consumables.finance.receivable.mapper.ReceivableMapper;
import com.company.consumables.finance.report.service.ReportService;
import com.company.consumables.finance.report.vo.ExpenseSummaryVo;
import com.company.consumables.finance.report.vo.FundSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 类描述: 经营报表服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final FundFlowMapper fundFlowMapper;
    private final ReceivableMapper receivableMapper;
    private final PayableMapper payableMapper;

    /**
     * 功能描述: 资金汇总
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 资金汇总
     * @author honghui
     * @date 2026/07/06 12:28
     */
    @Override
    public FundSummaryVo fundSummary(Date startDate, Date endDate) {
        long income = fundFlowMapper.sumAmountByDirection(FundDirection.INCOME.getCode(), startDate, endDate);
        long expense = fundFlowMapper.sumAmountByDirection(FundDirection.EXPENSE.getCode(), startDate, endDate);
        FundSummaryVo vo = new FundSummaryVo();
        vo.setIncomeTotal(income);
        vo.setExpenseTotal(expense);
        vo.setNetAmount(income - expense);
        vo.setUnreceivedTotal(receivableMapper.sumUnreceived());
        vo.setUnpaidTotal(payableMapper.sumUnpaid());
        return vo;
    }

    /**
     * 功能描述: 费用/收入分类汇总
     *
     * @param categoryType 分类类型：1支出 2收入
     * @param startDate    起始日期
     * @param endDate      结束日期
     * @return 分类汇总列表
     * @author honghui
     * @date 2026/07/06 12:28
     */
    @Override
    public List<ExpenseSummaryVo> expenseSummary(int categoryType, Date startDate, Date endDate) {
        // 支出分类看费用支出流水，收入分类看其他收入流水
        int flowType = categoryType == CategoryType.INCOME.getCode()
                ? FundFlowType.OTHER_INCOME.getCode() : FundFlowType.EXPENSE.getCode();
        return fundFlowMapper.summaryByCategory(flowType, startDate, endDate);
    }
}
