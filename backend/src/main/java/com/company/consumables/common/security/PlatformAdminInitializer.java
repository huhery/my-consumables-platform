package com.company.consumables.common.security;

import com.company.consumables.common.enums.RoleType;
import com.company.consumables.common.enums.TenantStatus;
import com.company.consumables.platform.account.entity.Account;
import com.company.consumables.platform.account.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 类描述: 平台管理员账号初始化器。启动时若不存在则创建默认平台管理员（密码 BCrypt 加密）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlatformAdminInitializer implements ApplicationRunner {

    /** 默认平台管理员登录名 */
    @Value("${consumables.platform-admin.login-name:admin}")
    private String adminLoginName;

    /** 默认平台管理员初始密码 */
    @Value("${consumables.platform-admin.password:admin123}")
    private String adminPassword;

    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 功能描述: 启动时确保平台管理员账号存在
     *
     * @param args 启动参数
     * @author honghui
     * @date 2026/07/12 13:00
     */
    @Override
    public void run(ApplicationArguments args) {
        if (accountMapper.countByLoginName(adminLoginName) > 0) {
            return;
        }
        Account admin = new Account();
        admin.setSLoginName(adminLoginName);
        admin.setSPassword(passwordEncoder.encode(adminPassword));
        admin.setSTenantId("");
        admin.setIRole(RoleType.PLATFORM_ADMIN.getCode());
        admin.setIStatus(TenantStatus.ENABLED.getCode());
        accountMapper.insert(admin);
        log.info("已初始化平台管理员账号: {}", adminLoginName);
    }
}
