package com.company.consumables.finance.fund.service;

import com.company.consumables.common.enums.FundDirection;
import com.company.consumables.common.enums.FundFlowType;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.finance.fund.entity.FundFlow;
import com.company.consumables.finance.fund.vo.FundFlowQueryVo;

import java.util.Date;

/**
 * 类描述: 资金流水服务接口，统一承载各类资金进出流水的写入与查询
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface FundFlowService {

    /**
     * 功能描述: 写入一条资金流水
     *
     * @param flowType   流水类型
     * @param direction  方向
     * @param amount     金额（分，正数）
     * @param partyId    关联对象ID（客户/供应商，可空）
     * @param sourceNo   关联单据ID（可空）
     * @param categoryId 费用/收入分类ID（可空）
     * @param occurDate  发生日期
     * @param remark     备注
     * @author honghui
     * @date 2026/07/06 10:20
     */
    void record(FundFlowType flowType, FundDirection direction, int amount,
                String partyId, String sourceNo, String categoryId, Date occurDate, String remark);

    /**
     * 功能描述: 分页查询资金流水
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 10:20
     */
    PageResult<FundFlow> pageFlow(FundFlowQueryVo query);
}
