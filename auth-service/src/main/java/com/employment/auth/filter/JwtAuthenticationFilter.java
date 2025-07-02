package com.employment.auth.filter;

import com.employment.auth.model.User;
import com.employment.auth.service.UserService;
import com.employment.auth.util.AuthConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;
    
    @Value("${jwt.header}")
    private String tokenHeader;
    
    @Value("${jwt.token-prefix}")
    private String tokenPrefix;
    
    // 不需要验证令牌的路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/health",
            "/auth/validate",
            "/auth/verifyToken"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 跨域预检请求直接放行
        if (request.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }
        
        // 不需要验证令牌的路径直接放行
        String path = request.getRequestURI();
        if (EXCLUDE_PATHS.stream().anyMatch(path::endsWith)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 获取请求头中的令牌
        String authHeader = request.getHeader(tokenHeader);
        
        // 如果请求头中没有令牌，返回未授权错误
        if (authHeader == null || !authHeader.startsWith(tokenPrefix)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
            return;
        }
        
        // 提取令牌
        String token = authHeader.substring(tokenPrefix.length()).trim();
        
        // 验证令牌
        if (!userService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"令牌无效或已过期\"}");
            return;
        }
        
        // 获取用户信息
        User user = userService.getUserByToken(token);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"用户不存在\"}");
            return;
        }
        
        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == AuthConstant.UserStatus.DISABLED) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"code\":403,\"message\":\"账号已被禁用\"}");
            return;
        }
        
        // 将用户信息设置到请求属性中，方便后续使用
        request.setAttribute("userId", user.getId());
        request.setAttribute("username", user.getUsername());
        request.setAttribute("userType", user.getUserType());
        
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
} 