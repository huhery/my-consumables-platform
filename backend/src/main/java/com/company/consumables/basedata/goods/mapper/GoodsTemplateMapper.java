package com.company.consumables.basedata.goods.mapper;

import com.company.consumables.basedata.goods.entity.GoodsTemplate;

import java.util.List;

/**
 * 类描述: 产品模板 Mapper（平台级表，不参与租户隔离）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/15
 */
public interface GoodsTemplateMapper {

    /**
     * 功能描述: 查询全部产品模板
     *
     * @return 模板列表
     * @author honghui
     * @date 2026/07/15 10:00
     */
    List<GoodsTemplate> selectAll();
}
