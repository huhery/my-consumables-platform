package com.company.consumables.basedata.goods.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 类描述: 商品新增/修改入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class GoodsSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，修改时必填 */
    private String sId;

    /** 商品编码 */
    @NotBlank(message = "商品编码不能为空")
    private String sCode;

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String sName;

    /** 商品分类 */
    private String sCategory;

    /** 规格 */
    private String sSpec;

    /** 基本单位名称 */
    @NotBlank(message = "基本单位不能为空")
    private String sBaseUnit;
}
