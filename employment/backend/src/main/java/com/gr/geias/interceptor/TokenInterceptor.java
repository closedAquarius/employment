package com.gr.geias.interceptor;

import com.gr.geias.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RestTemplate restTemplate;
    
    // 统一认证服务地址
    private static final String AUTH_SERVICE_URL = "http://localhost:8090/api/auth/verify-token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
            return false;
        }

        try {
            // 调用统一认证服务验证token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                AUTH_SERVICE_URL, 
                HttpMethod.POST, 
                entity, 
                Map.class
            );
            
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.get("code").equals(200)) {
                // 令牌有效，将用户信息存入请求属性
                if (responseBody.containsKey("userInfo")) {
                    request.setAttribute("userInfo", responseBody.get("userInfo"));
                }
                return true;
            } else {
                response.setStatus(401);
                response.getWriter().write("{\"code\":401,\"message\":\"令牌无效或已过期\"}");
                return false;
            }
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"令牌验证失败: " + e.getMessage() + "\"}");
            return false;
        }
    }
}
