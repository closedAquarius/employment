package com.guangge.Interview.auth;

import com.guangge.Interview.exception.RestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 认证拦截器，用于验证请求中的JWT令牌
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    private final AuthClient authClient;
    
    // 定义无需认证的路径
    private static final List<String> EXEMPT_PATHS = Arrays.asList(
        "/interview/face2faceChat",
        "/interview/face2faceChatBytes",
        "/interview/chat",
        "/interview/welcomemp3"
    );

    public AuthInterceptor(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        logger.debug("处理请求: {}", requestPath);
        
        // 检查是否是豁免路径
        for (String exemptPath : EXEMPT_PATHS) {
            if (requestPath.contains(exemptPath)) {
                logger.debug("路径 {} 无需认证", requestPath);
                return true;
            }
        }
        
        // 从请求头中获取令牌
        String token = extractTokenFromRequest(request);
        
        // 如果没有令牌，则拒绝请求
        if (token == null) {
            logger.warn("请求 {} 未提供认证令牌", requestPath);
            throw new RestException("401", "未提供认证令牌");
        }

        // 验证令牌
        Map<String, Object> userInfo = authClient.validateToken(token);
        if (userInfo == null) {
            logger.warn("请求 {} 提供的认证令牌无效", requestPath);
            throw new RestException("401", "认证令牌无效或已过期");
        }
        
        // 将用户信息存储到请求属性中，以便后续使用
        request.setAttribute("userId", userInfo.get("userId"));
        request.setAttribute("username", userInfo.get("username"));
        request.setAttribute("userType", userInfo.get("userType"));

        logger.debug("认证成功: userId={}, username={}", userInfo.get("userId"), userInfo.get("username"));
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