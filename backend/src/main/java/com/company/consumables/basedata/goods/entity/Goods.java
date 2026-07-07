package com.company.consumables.basedata.goods.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 商品档案实体，对应表 TAB_GOODS
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Goods extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 商品编码 */
    private String sCode;

    /** 商品名称 */
    private String sName;

    /** 商品分类 */
    private String sCategory;

    /** 规格 */
    private String sSpec;

    /** 基本单位名称 */
    private String sBaseUnit;

    /** 状态：0停用 1启用 */
    private Integer iStatus;
}
