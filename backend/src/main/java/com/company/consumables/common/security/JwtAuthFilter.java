package com.company.consumables.common.security;

import com.company.consumables.common.context.UserContext;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.error.ErrorCodeMessageHolder;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.common.tenant.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 类描述: JWT 认证过滤器。解析令牌建立租户/用户上下文；登录等匿名接口放行；
 *         无效令牌返回未认证。请求结束清理 ThreadLocal
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /** 令牌请求头 */
    private static final String HEADER = "Authorization";

    /** 令牌前缀 */
    private static final String BEARER = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ErrorCodeMessageHolder errorCodeMessageHolder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 功能描述: 判断是否匿名放行路径（登录接口、错误页）
     *
     * @param uri 请求路径
     * @return true 表示匿名放行
     * @author honghui
     * @date 2026/07/12 12:30
     */
    private boolean isAnonymous(String uri) {
        return uri.startsWith("/api/auth/login")
                || uri.startsWith("/api/auth/logout")
                || uri.equals("/error");
    }

    /**
     * 功能描述: 过滤逻辑
     *
     * @param request  请求
     * @param response 响应
     * @param chain    过滤链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     * @author honghui
     * @date 2026/07/12 12:30
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (isAnonymous(uri)) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HEADER);
        if (header == null || !header.startsWith(BEARER)) {
            writeUnauthorized(response);
            return;
        }
        String token = header.substring(BEARER.length());
        try {
            Claims claims = jwtUtil.parse(token);
            String userId = claims.getSubject();
            String tenantId = claims.get(JwtUtil.CLAIM_TENANT_ID, String.class);
            // 建立上下文
            UserContext.setCurrentUser(userId);
            if (tenantId != null && !tenantId.isEmpty()) {
                TenantContext.setTenantId(tenantId);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.warn("令牌校验失败: {}", e.getMessage());
            writeUnauthorized(response);
        } finally {
            // 请求结束清理 ThreadLocal，防止线程复用脏数据
            UserContext.clear();
            TenantContext.clear();
        }
    }

    /**
     * 功能描述: 输出未认证响应
     *
     * @param response 响应
     * @throws IOException IO 异常
     * @author honghui
     * @date 2026/07/12 12:30
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String msg = errorCodeMessageHolder.getMessage(ErrorCode.AUTH_UNAUTHENTICATED);
        RestApiResultVo<Void> body = RestApiResultVo.error(ErrorCode.AUTH_UNAUTHENTICATED, msg);
        response.getOutputStream().write(objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8));
    }
}
