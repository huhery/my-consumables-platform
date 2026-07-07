package com.company.consumables.finance.expense.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 类描述: 费用/收入分类新增入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Data
public class ExpenseCategorySaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，修改时必填 */
    private String sId;

    /** 分类名称 */
    @NotBlank(message = "分类名称不能为空")
    private String sName;

    /** 分类类型：1支出 2收入 */
    @NotNull(message = "分类类型不能为空")
    private Integer iCategoryType;
}
