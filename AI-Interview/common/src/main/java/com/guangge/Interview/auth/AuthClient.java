package com.guangge.Interview.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthClient.class);
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
            logger.debug("Sending token validation request to {}", authServiceUrl + "/auth/validate");
            ResponseEntity<Map> response = restTemplate.exchange(
                    authServiceUrl + "/auth/validate",
                HttpMethod.GET, 
                requestEntity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.debug("Token validation successful");
                return response.getBody();
            } else {
                logger.warn("Token validation failed with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            // 验证失败，记录日志
            logger.error("Token validation failed: {}", e.getMessage(), e);
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
            logger.debug("Sending login request for user: {}", username);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    authServiceUrl + "/auth/login",
                loginRequest, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.debug("Login successful for user: {}", username);
                return response.getBody();
            } else {
                logger.warn("Login failed for user: {} with status: {}", username, response.getStatusCode());
            }
        } catch (Exception e) {
            // 登录失败，记录日志
            logger.error("Login failed for user {}: {}", username, e.getMessage(), e);
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
            logger.debug("Sending token refresh request");
            ResponseEntity<Map> response = restTemplate.exchange(
                    authServiceUrl + "/auth/refresh",
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.debug("Token refresh successful");
                Map<String, String> responseBody = response.getBody();
                return responseBody != null ? responseBody.get("token") : null;
            } else {
                logger.warn("Token refresh failed with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            // 刷新失败，记录日志
            logger.error("Token refresh failed: {}", e.getMessage(), e);
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
            logger.debug("Syncing user to auth service: {}", user.get("username"));
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    authServiceUrl + "/auth/sync-user?sourceSystem=ai-interview",
                    user,
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.debug("User sync successful for: {}", user.get("username"));
                return response.getBody();
            } else {
                logger.warn("User sync failed for: {} with status: {}", user.get("username"), response.getStatusCode());
            }
        } catch (Exception e) {
            // 同步失败，记录日志
            logger.error("User sync failed for {}: {}", user.get("username"), e.getMessage(), e);
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