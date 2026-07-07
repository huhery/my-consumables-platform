package com.company.consumables.basedata.customer.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 类描述: 客户新增/修改入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class CustomerSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，修改时必填 */
    private String sId;

    /** 客户名称 */
    @NotBlank(message = "客户名称不能为空")
    private String sName;

    /** 联系人 */
    private String sContact;

    /** 收货地址 */
    private String sAddress;

    /** 联系电话 */
    private String sPhone;
}
