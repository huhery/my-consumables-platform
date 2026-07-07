package com.company.consumables.basedata.goods.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 商品分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 商品编码（模糊） */
    private String sCode;

    /** 商品名称（模糊） */
    private String sName;

    /** 商品分类 */
    private String sCategory;
}
