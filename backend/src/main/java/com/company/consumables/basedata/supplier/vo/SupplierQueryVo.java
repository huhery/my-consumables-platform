package com.company.consumables.basedata.supplier.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 供应商分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 供应商名称（模糊） */
    private String sName;
}
