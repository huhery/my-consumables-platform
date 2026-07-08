package com.company.consumables.ai.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: AI 问数出参。answer 为自然语言回答；rejected 表示未匹配意图的拒答；
 *         degraded 表示大模型不可用的降级；data 为结构化结果，供前端可选渲染。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Data
public class AiAnswerVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自然语言回答 */
    private String answer;

    /** 命中的意图编码（拒答/降级为空） */
    private String intent;

    /** 是否为拒答（匹配不到预设意图） */
    private boolean rejected;

    /** 是否为降级（大模型不可用） */
    private boolean degraded;

    /** 结构化数据（前端可选渲染），可为 null */
    private Object data;

    /**
     * 功能描述: 构造成功回答
     *
     * @param intent 意图编码
     * @param answer 回答文本
     * @param data   结构化数据
     * @return 出参
     * @author honghui
     * @date 2026/07/08 11:00
     */
    public static AiAnswerVo success(String intent, String answer, Object data) {
        AiAnswerVo vo = new AiAnswerVo();
        vo.setIntent(intent);
        vo.setAnswer(answer);
        vo.setData(data);
        return vo;
    }

    /**
     * 功能描述: 构造拒答（匹配不到预设意图）
     *
     * @param answer 提示可问范围的文本
     * @return 出参
     * @author honghui
     * @date 2026/07/08 11:00
     */
    public static AiAnswerVo reject(String answer) {
        AiAnswerVo vo = new AiAnswerVo();
        vo.setRejected(true);
        vo.setAnswer(answer);
        return vo;
    }

    /**
     * 功能描述: 构造降级回答（大模型不可用）
     *
     * @param answer 降级提示文本
     * @return 出参
     * @author honghui
     * @date 2026/07/08 11:00
     */
    public static AiAnswerVo degrade(String answer) {
        AiAnswerVo vo = new AiAnswerVo();
        vo.setDegraded(true);
        vo.setAnswer(answer);
        return vo;
    }
}
