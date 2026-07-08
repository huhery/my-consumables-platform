package com.company.consumables.ai.service;

import com.company.consumables.ai.vo.AiAnswerVo;

/**
 * 类描述: AI 智能问数编排服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
public interface AiAssistantService {

    /**
     * 功能描述: 处理自然语言问数：意图识别 → 路由取数 → 渲染回答；失败降级、映射不到拒答
     *
     * @param question 用户问题
     * @return 回答（成功/拒答/降级）
     * @author honghui
     * @date 2026/07/08 12:10
     */
    AiAnswerVo ask(String question);
}
