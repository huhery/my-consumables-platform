package com.company.consumables.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 类描述: JWT 工具，负责令牌签发与解析。令牌载荷含用户ID、租户ID、角色
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Component
public class JwtUtil {

    /** 签名密钥（生产应放配置/密钥管理，长度需≥32字节） */
    @Value("${consumables.jwt.secret:consumables-platform-secret-key-please-change-in-prod}")
    private String secret;

    /** 令牌有效期（毫秒），默认 7 天 */
    @Value("${consumables.jwt.expire-millis:604800000}")
    private long expireMillis;

    /** 载荷键：租户ID */
    public static final String CLAIM_TENANT_ID = "tenantId";

    /** 载荷键：角色 */
    public static final String CLAIM_ROLE = "role";

    /**
     * 功能描述: 获取签名密钥
     *
     * @return 密钥
     * @author honghui
     * @date 2026/07/12 11:30
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 功能描述: 签发令牌
     *
     * @param userId   用户ID（subject）
     * @param tenantId 租户ID（平台管理员可为空）
     * @param role     角色编码
     * @return JWT 字符串
     * @author honghui
     * @date 2026/07/12 11:30
     */
    public String generate(String userId, String tenantId, int role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expireMillis);
        return Jwts.builder()
                .setSubject(userId)
                .claim(CLAIM_TENANT_ID, tenantId == null ? "" : tenantId)
                .claim(CLAIM_ROLE, role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 功能描述: 解析令牌，非法或过期抛异常
     *
     * @param token JWT 字符串
     * @return 载荷 Claims
     * @author honghui
     * @date 2026/07/12 11:30
     */
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
