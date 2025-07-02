package com.guangge.Interview.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

/**
 * 认证客户端，用于与auth-service进行交互
 */
public class AuthClient {

    private static final Logger logger = LoggerFactory.getLogger(AuthClient.class);
    private final RestTemplate restTemplate;
    private final String authServiceUrl;
    private final String jwtSecret;

    public AuthClient(RestTemplate restTemplate, String authServiceUrl) {
        this(restTemplate, authServiceUrl, null);
    }
    
    public AuthClient(RestTemplate restTemplate, String authServiceUrl, String jwtSecret) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
        this.jwtSecret = jwtSecret != null ? jwtSecret : "eAf6XIz7Q6CmE3N4K5L6M7N8O9P0Q1R2S3T4U5V6W7X8Y9Z0aBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
    }

    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @return 用户信息，如果验证失败则返回null
     */
    public Map<String, Object> validateToken(String token) {
        try {
            logger.info("验证令牌: {}", token);
            
            // 首先尝试本地验证
            Map<String, Object> localValidation = validateTokenLocally(token);
            if (localValidation != null) {
                logger.info("本地令牌验证成功");
                return localValidation;
            }
            
            // 本地验证失败，尝试远程验证
            logger.info("本地验证失败，尝试远程验证");
            
            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            // 添加Bearer前缀，与auth-service的预期格式保持一致
            headers.set("Authorization", "Bearer " + token);
            
            // 创建请求实体
            HttpEntity<String> entity = new HttpEntity<>(headers);
        
            // 发送验证请求
            String validateUrl = authServiceUrl + "/auth/validate";
            logger.info("发送验证请求到: {}", validateUrl);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    validateUrl, 
                HttpMethod.GET, 
                    entity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("令牌验证成功: {}", response.getBody());
                return response.getBody();
            } else {
                logger.warn("令牌验证失败: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            logger.error("验证令牌时发生错误", e);
            return null;
        }
    }
    
    /**
     * 本地验证JWT令牌，不依赖auth-service
     * @param token JWT令牌
     * @return 用户信息，如果验证失败则返回null
     */
    private Map<String, Object> validateTokenLocally(String token) {
        try {
            // 使用Sign类验证令牌
            Claims claims = Sign.verifyToken(token, jwtSecret);
            
            // 检查令牌是否过期
            if (claims.getExpiration().before(new Date())) {
                logger.warn("令牌已过期");
                return null;
            }
            
            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            
            // 提取常用字段
            if (claims.get("userId") != null) {
                userInfo.put("userId", claims.get("userId"));
            }
            
            if (claims.getSubject() != null) {
                userInfo.put("username", claims.getSubject());
            }
            
            if (claims.get("userType") != null) {
                userInfo.put("userType", claims.get("userType"));
            }
            
            if (claims.get("realName") != null) {
                userInfo.put("realName", claims.get("realName"));
            }
            
            // 将所有其他字段也添加到用户信息中
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                if (!userInfo.containsKey(entry.getKey())) {
                    userInfo.put(entry.getKey(), entry.getValue());
        }
            }
            
            return userInfo;
        } catch (Exception e) {
            logger.warn("本地验证令牌失败: {}", e.getMessage());
        return null;
        }
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