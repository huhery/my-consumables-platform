package com.company.consumables.platform.account.service.impl;

import com.company.consumables.common.enums.RoleType;
import com.company.consumables.common.enums.TenantStatus;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.security.JwtUtil;
import com.company.consumables.platform.account.entity.Account;
import com.company.consumables.platform.account.mapper.AccountMapper;
import com.company.consumables.platform.account.service.AuthService;
import com.company.consumables.platform.account.vo.LoginResultVo;
import com.company.consumables.platform.account.vo.LoginVo;
import com.company.consumables.platform.tenant.entity.Tenant;
import com.company.consumables.platform.tenant.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 类描述: 认证服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountMapper accountMapper;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 功能描述: 登录。校验账号存在、密码正确、账号启用；商家账号还需校验其租户启用。
     *           凭据错误统一提示，不区分账号或密码错误（防枚举）
     *
     * @param vo 登录入参
     * @return 登录结果
     * @author honghui
     * @date 2026/07/12 12:15
     */
    @Override
    public LoginResultVo login(LoginVo vo) {
        Account account = accountMapper.selectByLoginName(vo.getLoginName());
        // 账号不存在或密码错误：统一提示
        if (account == null || !passwordEncoder.matches(vo.getPassword(), account.getSPassword())) {
            throw new BusinessException(ErrorCode.AUTH_FAILED);
        }
        // 账号被停用
        if (account.getIStatus() != null && account.getIStatus() == TenantStatus.DISABLED.getCode()) {
            throw new BusinessException(ErrorCode.AUTH_FAILED);
        }
        // 商家账号需校验其租户为启用状态
        if (account.getIRole() != null && account.getIRole() == RoleType.TENANT_ADMIN.getCode()
                && StringUtils.hasText(account.getSTenantId())) {
            Tenant tenant = tenantMapper.selectById(account.getSTenantId());
            if (tenant == null || tenant.getIStatus() == null
                    || tenant.getIStatus() == TenantStatus.DISABLED.getCode()) {
                throw new BusinessException(ErrorCode.TENANT_DISABLED);
            }
            // 校验到期日期
            if (tenant.getDExpireDate() != null && tenant.getDExpireDate().before(new java.util.Date())) {
                throw new BusinessException(ErrorCode.TENANT_EXPIRED);
            }
        }

        String token = jwtUtil.generate(account.getSId(), account.getSTenantId(), account.getIRole());
        LoginResultVo result = new LoginResultVo();
        result.setToken(token);
        result.setLoginName(account.getSLoginName());
        result.setRole(account.getIRole());
        result.setTenantId(account.getSTenantId());
        return result;
    }
}
