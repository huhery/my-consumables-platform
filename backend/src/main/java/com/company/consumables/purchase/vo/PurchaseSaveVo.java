package com.company.consumables.purchase.vo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 类描述: 进货单新增入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class PurchaseSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    @NotBlank(message = "供应商不能为空")
    private String sSupplierId;

    /** 入库地点ID */
    @NotBlank(message = "入库地点不能为空")
    private String sWarehouseId;

    /** 进货明细 */
    @NotEmpty(message = "进货明细不能为空")
    @Valid
    private List<PurchaseItemVo> items;
}
