package com.company.consumables.ai.entity;

import com.company.consumables.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: AI 问答日志实体，对应表 TAB_AI_CHAT_LOG（业务表，随租户隔离）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户问题 */
    private String sQuestion;

    /** 识别到的意图编码（拒答为空） */
    private String sIntent;

    /** 是否成功回答：0否 1是 */
    private Integer iSuccess;

    /** 是否降级：0否 1是 */
    private Integer iDegraded;
}
