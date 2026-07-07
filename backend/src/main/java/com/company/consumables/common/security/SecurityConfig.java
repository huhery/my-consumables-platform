package com.company.consumables.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 类描述: 安全相关 Bean 配置（仅使用 BCrypt 密码编码器，不引入完整 Spring Security 过滤链）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Configuration
public class SecurityConfig {

    /**
     * 功能描述: 提供 BCrypt 密码编码器
     *
     * @return 密码编码器
     * @author honghui
     * @date 2026/07/12 11:50
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
