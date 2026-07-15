package com.company.consumables.basedata.goods.mapper;

import com.company.consumables.basedata.goods.entity.GoodsUnitTemplate;

import java.util.List;

/**
 * 类描述: 产品包装单位模板 Mapper（平台级表）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/15
 */
public interface GoodsUnitTemplateMapper {

    /**
     * 功能描述: 查询全部包装单位模板
     *
     * @return 模板列表
     * @author honghui
     * @date 2026/07/15 22:00
     */
    List<GoodsUnitTemplate> selectAll();
}
