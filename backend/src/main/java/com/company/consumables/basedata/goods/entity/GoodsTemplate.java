package com.company.consumables.basedata.goods.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 产品模板实体（平台级，不参与租户隔离），对应表 TAB_GOODS_TEMPLATE。
 *         商家开通时系统自动将模板复制到其商品表。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/15
 */
@Data
public class GoodsTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String sId;

    /** 产品编码 */
    private String sCode;

    /** 分类 */
    private String sCategory;

    /** 产品名称 */
    private String sName;

    /** 规格 */
    private String sSpec;

    /** 基本单位 */
    private String sBaseUnit;
}
