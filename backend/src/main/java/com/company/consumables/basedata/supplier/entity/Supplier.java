package com.company.consumables.basedata.supplier.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 供应商实体，对应表 TAB_SUPPLIER
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Supplier extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 供应商名称 */
    private String sName;

    /** 联系人 */
    private String sContact;

    /** 联系电话 */
    private String sPhone;
}
