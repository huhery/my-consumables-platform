package com.company.consumables.ai.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 类描述: AI 问数入参
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Data
public class AiAskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户自然语言问题 */
    @NotBlank(message = "请输入您要查询的问题")
    private String question;
}
