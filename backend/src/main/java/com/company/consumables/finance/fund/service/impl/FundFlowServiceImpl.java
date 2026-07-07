package com.company.consumables.finance.fund.service.impl;

import com.company.consumables.common.enums.FundDirection;
import com.company.consumables.common.enums.FundFlowType;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.util.ValueUtil;
import com.company.consumables.finance.fund.entity.FundFlow;
import com.company.consumables.finance.fund.mapper.FundFlowMapper;
import com.company.consumables.finance.fund.service.FundFlowService;
import com.company.consumables.finance.fund.vo.FundFlowQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 类描述: 资金流水服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Service
@RequiredArgsConstructor
public class FundFlowServiceImpl implements FundFlowService {

    private final FundFlowMapper fundFlowMapper;

    /**
     * 功能描述: 写入一条资金流水
     *
     * @param flowType   流水类型
     * @param direction  方向
     * @param amount     金额（分）
     * @param partyId    关联对象ID
     * @param sourceNo   关联单据ID
     * @param categoryId 分类ID
     * @param occurDate  发生日期
     * @param remark     备注
     * @author honghui
     * @date 2026/07/06 10:22
     */
    @Override
    public void record(FundFlowType flowType, FundDirection direction, int amount,
                       String partyId, String sourceNo, String categoryId, Date occurDate, String remark) {
        FundFlow flow = new FundFlow();
        flow.setIFlowType(flowType.getCode());
        flow.setIDirection(direction.getCode());
        flow.setIAmount(amount);
        flow.setSPartyId(ValueUtil.defaultEmpty(partyId));
        flow.setSSourceNo(ValueUtil.defaultEmpty(sourceNo));
        flow.setSCategoryId(ValueUtil.defaultEmpty(categoryId));
        flow.setDOccurDate(occurDate == null ? new Date() : occurDate);
        flow.setSRemark(ValueUtil.defaultEmpty(remark));
        fundFlowMapper.insert(flow);
    }

    /**
     * 功能描述: 分页查询资金流水
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 10:22
     */
    @Override
    public PageResult<FundFlow> pageFlow(FundFlowQueryVo query) {
        long total = fundFlowMapper.countPage(query);
        List<FundFlow> list = fundFlowMapper.selectPage(query);
        return PageResult.of(total, list);
    }
}
