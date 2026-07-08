package com.company.consumables.ai.intent;

import com.company.consumables.ai.vo.AiAnswerVo;

import java.util.Map;

/**
 * 类描述: 预设意图处理器接口。每个实现代表一个"可回答的问题类型"，
 *         负责调用既有业务查询（自动经租户隔离）并把结果渲染为自然语言回答。
 *         新增一个可回答的问题 = 新增一个本接口的 Spring 实现，路由核心零改动。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
public interface IntentHandler {

    /**
     * 功能描述: 意图唯一编码（大写下划线，如 SALES_AMOUNT），供大模型选择与路由分发
     *
     * @return 意图编码
     * @author honghui
     * @date 2026/07/08 11:10
     */
    String intentCode();

    /**
     * 功能描述: 意图说明（给大模型看，用于判断该意图适用的问题）
     *
     * @return 意图说明
     * @author honghui
     * @date 2026/07/08 11:10
     */
    String description();

    /**
     * 功能描述: 该意图需要的参数说明（给大模型抽参用；无参数返回空字符串）
     *
     * @return 参数说明文本
     * @author honghui
     * @date 2026/07/08 11:10
     */
    String paramsHint();

    /**
     * 功能描述: 执行取数并渲染回答。取数走既有 Service，自动经租户隔离拦截器
     *
     * @param params 大模型抽取的参数（可能为空）
     * @return 自然语言回答 + 结构化数据
     * @author honghui
     * @date 2026/07/08 11:10
     */
    AiAnswerVo handle(Map<String, Object> params);
}
