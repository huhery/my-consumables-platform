package com.company.consumables.basedata.warehouse.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 类描述: 库存地点新增/修改入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class WarehouseSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，修改时必填 */
    private String sId;

    /** 地点编码 */
    @NotBlank(message = "地点编码不能为空")
    private String sCode;

    /** 地点名称 */
    @NotBlank(message = "地点名称不能为空")
    private String sName;

    /** 地点类型：1仓库 2门店 */
    @NotNull(message = "地点类型不能为空")
    private Integer iLocationType;
}
