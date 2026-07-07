package com.company.consumables.basedata.customer.vo;

import com.company.consumables.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 客户分页查询入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerQueryVo extends PageQuery {

    private static final long serialVersionUID = 1L;

    /** 客户名称（模糊） */
    private String sName;

    /** 联系人（模糊） */
    private String sContact;

    /** 联系电话（模糊） */
    private String sPhone;
}
