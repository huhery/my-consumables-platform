package com.company.consumables.basedata.warehouse.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 库存地点分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 地点名称（模糊） */
    private String sName;

    /** 地点类型：1仓库 2门店 */
    private Integer iLocationType;
}
