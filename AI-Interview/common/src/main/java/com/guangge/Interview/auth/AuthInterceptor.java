package com.guangge.Interview.auth;

import com.guangge.Interview.exception.RestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 认证拦截器，用于验证请求的JWT令牌
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthClient authClient;
    private final List<String> excludePaths;

    public AuthInterceptor(AuthClient authClient, String... excludePaths) {
        this.authClient = authClient;
        this.excludePaths = Arrays.asList(excludePaths);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查是否为排除路径
        String requestPath = request.getRequestURI();
        if (isExcludedPath(requestPath)) {
            return true;
        }

        // 从请求头中获取token
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            throw new RestException("401", "未提供认证令牌");
        }

        // 验证token
        boolean isValid = authClient.verifyToken(token);
        if (!isValid) {
            throw new RestException("401", "认证令牌无效或已过期");
        }

        return true;
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String extractToken(HttpServletRequest request) {
        // 首先尝试从Authorization头获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 然后尝试从cookie获取
        String cookieToken = Sessions.getToken(request);
        if (StringUtils.hasText(cookieToken)) {
            return cookieToken;
        }
        
        return null;
    }

    /**
     * 检查请求路径是否在排除列表中
     */
    private boolean isExcludedPath(String requestPath) {
        for (String excludePath : excludePaths) {
            if (requestPath.startsWith(excludePath)) {
                return true;
            }
        }
        return false;
    }
} 