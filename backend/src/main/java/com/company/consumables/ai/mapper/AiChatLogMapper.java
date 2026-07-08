package com.company.consumables.ai.mapper;

import com.company.consumables.ai.entity.AiChatLog;

/**
 * 类描述: AI 问答日志 Mapper（业务表，随租户隔离拦截器自动过滤/填充）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
public interface AiChatLogMapper {

    /**
     * 功能描述: 新增问答日志（主键/审计/租户字段由拦截器自动填充）
     *
     * @param log 日志实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/08 12:00
     */
    int insert(AiChatLog log);
}
