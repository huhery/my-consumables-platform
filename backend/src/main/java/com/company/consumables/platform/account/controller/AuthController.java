package com.company.consumables.platform.account.controller;

import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.platform.account.service.AuthService;
import com.company.consumables.platform.account.vo.LoginResultVo;
import com.company.consumables.platform.account.vo.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类描述: 认证 REST 接口（登录接口允许匿名访问）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 功能描述: 登录
     *
     * @param vo 登录入参
     * @return 登录结果（含令牌）
     * @author honghui
     * @date 2026/07/12 12:20
     */
    @PostMapping("/login")
    public RestApiResultVo<LoginResultVo> login(@RequestBody @Valid LoginVo vo) {
        return RestApiResultVo.ok(authService.login(vo));
    }

    /**
     * 功能描述: 退出登录（无状态令牌，前端清除即可；此接口用于统一入口）
     *
     * @return 空结果
     * @author honghui
     * @date 2026/07/12 12:20
     */
    @PostMapping("/logout")
    public RestApiResultVo<Void> logout() {
        return RestApiResultVo.ok();
    }
}
