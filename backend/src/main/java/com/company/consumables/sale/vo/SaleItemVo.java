package com.company.consumables.sale.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 类描述: 销售明细入参（批发与散卖共用）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class SaleItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    @NotBlank(message = "商品不能为空")
    private String sGoodsId;

    /** 录入单位名称（散卖固定为基本单位） */
    @NotBlank(message = "单位不能为空")
    private String sInputUnit;

    /** 录入数量 */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer iInputQty;

    /** 售价（分，按基本单位计） */
    @NotNull(message = "售价不能为空")
    @Min(value = 0, message = "售价不能为负")
    private Integer iPrice;
}
