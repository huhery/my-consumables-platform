package com.company.consumables.finance.fund.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 类描述: 资金流水实体，对应表 TAB_FUND_FLOW
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FundFlow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 流水类型：1销售收款 2采购付款 3费用支出 4其他收入 */
    private Integer iFlowType;

    /** 方向：1收入 2支出 */
    private Integer iDirection;

    /** 金额（分，正数） */
    private Integer iAmount;

    /** 关联对象ID（客户/供应商） */
    private String sPartyId;

    /** 关联单据ID（销售单/进货单，可空） */
    private String sSourceNo;

    /** 费用/收入分类ID */
    private String sCategoryId;

    /** 发生日期 */
    private Date dOccurDate;

    /** 备注 */
    private String sRemark;
}
