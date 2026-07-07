package com.company.consumables.basedata.customer.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 客户（超市）实体，对应表 TAB_CUSTOMER
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 客户名称 */
    private String sName;

    /** 联系人 */
    private String sContact;

    /** 收货地址 */
    private String sAddress;

    /** 联系电话 */
    private String sPhone;
}
