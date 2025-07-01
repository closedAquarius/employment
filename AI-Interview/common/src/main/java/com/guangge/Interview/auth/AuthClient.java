package com.guangge.Interview.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证客户端，用于与auth-service进行交互
 */
public class AuthClient {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public AuthClient(RestTemplate restTemplate, String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    /**
     * 验证令牌有效性
     * @param token JWT令牌
     * @return 验证结果，包含用户信息
     */
    public Map<String, Object> validateToken(String token) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
        
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    authServiceUrl + "/auth/validate",
                HttpMethod.GET, 
                requestEntity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            // 验证失败，记录日志
            System.err.println("Token validation failed: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包含token和用户信息
     */
    public Map<String, Object> login(String username, String password) {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("username", username);
            loginRequest.put("password", password);
            
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    authServiceUrl + "/auth/login",
                loginRequest, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            // 登录失败，记录日志
            System.err.println("Login failed: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 刷新令牌
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    authServiceUrl + "/auth/refresh",
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, String> responseBody = response.getBody();
                return responseBody != null ? responseBody.get("token") : null;
            }
        } catch (Exception e) {
            // 刷新失败，记录日志
            System.err.println("Token refresh failed: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 同步用户信息到认证服务
     * @param user 用户信息
     * @return 同步结果
     */
    public Map<String, Object> syncUser(Map<String, Object> user) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    authServiceUrl + "/auth/sync-user?sourceSystem=ai-interview",
                    user,
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            // 同步失败，记录日志
            System.err.println("User sync failed: " + e.getMessage());
        }
        
        return null;
        }
    
    /**
     * 同步用户信息到认证服务
     * @param userId 用户ID
     * @param username 用户名
     * @param realName 真实姓名
     * @return 同步结果
     */
    public Map<String, Object> syncUser(Long userId, String username, String realName) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("username", username);
        user.put("realName", realName);
        
        return syncUser(user);
    }
} 