package com.guangge.Interview.auth;

import com.guangge.Interview.exception.RestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 认证拦截器，用于验证请求中的JWT令牌
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    private final AuthClient authClient;
    
    public AuthInterceptor(AuthClient authClient) {
        this.authClient = authClient;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取令牌
        String token = extractTokenFromRequest(request);
        
        // 如果没有令牌，则拒绝请求
        if (token == null) {
            throw new RestException("401", "未提供认证令牌");
        }
        
        // 验证令牌
        Map<String, Object> userInfo = authClient.validateToken(token);
        if (userInfo == null) {
            throw new RestException("401", "认证令牌无效或已过期");
        }
        
        // 将用户信息存储到请求属性中，以便后续使用
        request.setAttribute("userId", userInfo.get("userId"));
        request.setAttribute("username", userInfo.get("username"));
        request.setAttribute("userType", userInfo.get("userType"));
        
        return true;
    }
    
    /**
     * 从请求中提取JWT令牌
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 