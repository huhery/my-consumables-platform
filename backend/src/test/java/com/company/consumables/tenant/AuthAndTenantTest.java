package com.company.consumables.tenant;

import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.platform.account.service.AuthService;
import com.company.consumables.platform.account.vo.LoginResultVo;
import com.company.consumables.platform.account.vo.LoginVo;
import com.company.consumables.platform.tenant.service.TenantService;
import com.company.consumables.platform.tenant.vo.TenantOpenVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 类描述: 认证与平台租户管理测试：开通商家、登录、停用租户拒登、登录名重复、密码错误
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthAndTenantTest {

    @Autowired
    private TenantService tenantService;
    @Autowired
    private AuthService authService;

    private TenantOpenVo openVo(String name, String loginName, String password) {
        TenantOpenVo vo = new TenantOpenVo();
        vo.setName(name);
        vo.setLoginName(loginName);
        vo.setPassword(password);
        return vo;
    }

    private LoginVo loginVo(String loginName, String password) {
        LoginVo vo = new LoginVo();
        vo.setLoginName(loginName);
        vo.setPassword(password);
        return vo;
    }

    /**
     * 功能描述: 开通商家后可用初始账号登录，返回令牌与租户
     *
     * @author honghui
     * @date 2026/07/12 13:45
     */
    @Test
    void openTenant_thenLogin_success() {
        String tenantId = tenantService.openTenant(openVo("商家甲", "jiashop", "pass123"));
        assertNotNull(tenantId);

        LoginResultVo result = authService.login(loginVo("jiashop", "pass123"));
        assertNotNull(result.getToken());
        assertEquals(tenantId, result.getTenantId());
    }

    /**
     * 功能描述: 密码错误登录被拒（统一提示）
     *
     * @author honghui
     * @date 2026/07/12 13:45
     */
    @Test
    void login_wrongPassword_shouldThrow() {
        tenantService.openTenant(openVo("商家乙", "yishop", "right-pass"));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(loginVo("yishop", "wrong-pass")));
        assertEquals(ErrorCode.AUTH_FAILED, ex.getErrorCode());
    }

    /**
     * 功能描述: 登录名重复开通被拒
     *
     * @author honghui
     * @date 2026/07/12 13:45
     */
    @Test
    void openTenant_duplicateLoginName_shouldThrow() {
        tenantService.openTenant(openVo("商家丙", "bingshop", "p1"));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> tenantService.openTenant(openVo("商家丙2", "bingshop", "p2")));
        assertEquals(ErrorCode.LOGIN_NAME_DUPLICATE, ex.getErrorCode());
    }

    /**
     * 功能描述: 停用租户后其账号无法登录
     *
     * @author honghui
     * @date 2026/07/12 13:45
     */
    @Test
    void disabledTenant_cannotLogin() {
        String tenantId = tenantService.openTenant(openVo("商家丁", "dingshop", "p1"));
        tenantService.disable(tenantId);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(loginVo("dingshop", "p1")));
        assertEquals(ErrorCode.TENANT_DISABLED, ex.getErrorCode());
    }
}
