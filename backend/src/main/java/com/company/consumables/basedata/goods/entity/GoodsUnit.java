package com.company.consumables.basedata.goods.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 商品包装单位换算实体，对应表 TAB_GOODS_UNIT
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsUnit extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 所属商品ID */
    private String sGoodsId;

    /** 包装单位名称 */
    private String sUnitName;

    /** 换算率：1包装单位=N基本单位 */
    private Integer iConvertRate;
}
