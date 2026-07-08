package com.company.consumables.ai.controller;

import com.company.consumables.ai.service.AiAssistantService;
import com.company.consumables.ai.vo.AiAnswerVo;
import com.company.consumables.ai.vo.AiAskVo;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.common.tenant.TenantContext;
import com.company.consumables.platform.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述: AI 智能问数 REST 接口。需登录（JWT 过滤器保证），且当前商家须开通 AI。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAssistantService aiAssistantService;
    private final TenantService tenantService;

    /**
     * 功能描述: 自然语言问数
     *
     * @param vo 问数入参
     * @return 回答（成功/拒答/降级）
     * @author honghui
     * @date 2026/07/08 12:25
     */
    @PostMapping("/ask")
    public RestApiResultVo<AiAnswerVo> ask(@RequestBody @Valid AiAskVo vo) {
        checkAiEnabled();
        if (!StringUtils.hasText(vo.getQuestion())) {
            throw new BusinessException(ErrorCode.AI_QUESTION_EMPTY);
        }
        return RestApiResultVo.ok(aiAssistantService.ask(vo.getQuestion().trim()));
    }

    /**
     * 功能描述: 查询当前商家是否开通 AI（供前端决定是否显示入口）
     *
     * @return { enabled: true/false }
     * @author honghui
     * @date 2026/07/08 12:25
     */
    @GetMapping("/status")
    public RestApiResultVo<Map<String, Object>> status() {
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", currentTenantAiEnabled());
        return RestApiResultVo.ok(data);
    }

    /**
     * 功能描述: 校验当前商家已开通 AI，否则拒绝
     *
     * @author honghui
     * @date 2026/07/08 12:25
     */
    private void checkAiEnabled() {
        if (!currentTenantAiEnabled()) {
            throw new BusinessException(ErrorCode.AI_NOT_ENABLED);
        }
    }

    /**
     * 功能描述: 判断当前租户是否开通 AI
     *
     * @return true 已开通
     * @author honghui
     * @date 2026/07/08 12:25
     */
    private boolean currentTenantAiEnabled() {
        String tenantId = TenantContext.getTenantId();
        return StringUtils.hasText(tenantId) && tenantService.isAiEnabled(tenantId);
    }
}
