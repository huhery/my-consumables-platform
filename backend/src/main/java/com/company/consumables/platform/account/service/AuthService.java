package com.company.consumables.platform.account.service;

import com.company.consumables.platform.account.vo.LoginResultVo;
import com.company.consumables.platform.account.vo.LoginVo;

/**
 * 类描述: 认证服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
public interface AuthService {

    /**
     * 功能描述: 登录，校验凭据与租户状态，返回 JWT
     *
     * @param vo 登录入参
     * @return 登录结果（含令牌）
     * @author honghui
     * @date 2026/07/12 12:10
     */
    LoginResultVo login(LoginVo vo);
}
