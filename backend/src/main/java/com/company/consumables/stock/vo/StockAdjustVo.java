package com.company.consumables.stock.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 类描述: 手工库存调整入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class StockAdjustVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 库存地点ID */
    @NotBlank(message = "库存地点不能为空")
    private String sWarehouseId;

    /** 商品ID */
    @NotBlank(message = "商品不能为空")
    private String sGoodsId;

    /** 调整数量（可正可负，基本单位） */
    @NotNull(message = "调整数量不能为空")
    private Integer iChangeQty;

    /** 调整原因 */
    @NotBlank(message = "调整原因不能为空")
    private String sReason;
}
