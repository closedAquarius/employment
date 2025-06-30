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

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 验证令牌
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean verifyToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                "/auth/validate", 
                HttpMethod.GET, 
                requestEntity, 
                Map.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取用户信息
     * @param token JWT令牌
     * @return 用户信息
     */
    public Map<String, Object> getUserInfo(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                "/auth/validate", 
                HttpMethod.GET, 
                requestEntity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            // 记录异常但不抛出
        }
        return new HashMap<>();
    }
    
    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回token和用户信息，失败返回null
     */
    public Map<String, Object> login(String username, String password) {
        try {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("username", username);
            loginRequest.put("password", password);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "/auth/login", 
                loginRequest, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            // 记录异常但不抛出
        }
        return null;
    }
    
    /**
     * 同步用户信息到auth-service
     * @param userInfo 用户信息
     * @return 同步结果
     */
    public boolean syncUserInfo(Map<String, Object> userInfo) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "/auth/sync-user?sourceSystem=ai-interview", 
                userInfo, 
                Map.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
} 