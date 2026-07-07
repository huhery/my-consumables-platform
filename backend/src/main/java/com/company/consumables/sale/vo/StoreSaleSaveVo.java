package com.company.consumables.sale.vo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 类描述: 门店散卖新增入参（出库地点必须为门店，明细按基本单位录入）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class StoreSaleSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 门店地点ID */
    @NotBlank(message = "门店不能为空")
    private String sWarehouseId;

    /** 散卖明细（按基本单位） */
    @NotEmpty(message = "销售明细不能为空")
    @Valid
    private List<SaleItemVo> items;
}
