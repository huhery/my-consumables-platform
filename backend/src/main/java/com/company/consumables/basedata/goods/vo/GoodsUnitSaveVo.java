package com.company.consumables.basedata.goods.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 类描述: 商品包装单位新增入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class GoodsUnitSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 包装单位名称 */
    @NotBlank(message = "包装单位名称不能为空")
    private String sUnitName;

    /** 换算率：1包装单位=N基本单位 */
    @NotNull(message = "换算率不能为空")
    private Integer iConvertRate;
}
