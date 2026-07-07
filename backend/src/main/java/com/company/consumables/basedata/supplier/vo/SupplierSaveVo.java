package com.company.consumables.basedata.supplier.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 类描述: 供应商新增/修改入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class SupplierSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，修改时必填 */
    private String sId;

    /** 供应商名称 */
    @NotBlank(message = "供应商名称不能为空")
    private String sName;

    /** 联系人 */
    private String sContact;

    /** 联系电话 */
    private String sPhone;
}
