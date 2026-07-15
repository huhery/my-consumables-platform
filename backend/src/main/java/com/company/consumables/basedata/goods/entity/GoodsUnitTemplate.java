package com.company.consumables.basedata.goods.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 产品包装单位模板实体（平台级），对应表 TAB_GOODS_UNIT_TEMPLATE。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/15
 */
@Data
public class GoodsUnitTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sId;

    /** 关联产品编码 */
    private String sGoodsCode;

    /** 包装单位名称 */
    private String sUnitName;

    /** 换算率 */
    private Integer iConvertRate;
}
