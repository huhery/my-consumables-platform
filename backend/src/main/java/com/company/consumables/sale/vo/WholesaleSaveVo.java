package com.company.consumables.sale.vo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 类描述: 批发销售单新增入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class WholesaleSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 客户ID */
    @NotBlank(message = "客户不能为空")
    private String sCustomerId;

    /** 出库地点ID */
    @NotBlank(message = "出库地点不能为空")
    private String sWarehouseId;

    /** 期望送达日期（可选，用于送货提醒） */
    private java.util.Date dExpectDelivery;

    /** 销售明细 */
    @NotEmpty(message = "销售明细不能为空")
    @Valid
    private List<SaleItemVo> items;
}
