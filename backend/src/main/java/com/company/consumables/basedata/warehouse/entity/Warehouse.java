package com.company.consumables.basedata.warehouse.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 库存地点实体，对应表 TAB_WAREHOUSE
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 地点编码 */
    private String sCode;

    /** 地点名称 */
    private String sName;

    /** 地点类型：1仓库 2门店 */
    private Integer iLocationType;

    /** 状态：0停用 1启用 */
    private Integer iStatus;
}
