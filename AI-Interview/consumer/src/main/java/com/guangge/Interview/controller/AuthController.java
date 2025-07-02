package com.guangge.Interview.controller;

import com.guangge.Interview.auth.Sign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController {

    @Autowired
    private RestTemplate restTemplate;
    
    // 统一认证服务地址
    private static final String AUTH_SERVICE_URL = "http://localhost:8095/api/auth";

    /**
     * 登录接口
     */
    @PostMapping("/interview/login")
    public Map<String, Object> login(@RequestParam String name, @RequestParam String code) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 临时登录逻辑，仅用于测试
            if ("admin".equals(name) && "admin".equals(code)) {
                result.put("code", 200);
                result.put("message", "登录成功");
                result.put("token", "test-token-" + UUID.randomUUID().toString());
                result.put("userId", 1);
                result.put("username", "admin");
                result.put("realName", "管理员");
                result.put("userType", 2);
                result.put("avatar", "");
                return result;
            }
            
            // 如果不是测试用户，尝试调用统一认证服务
            try {
                // 调用统一认证服务的登录接口
                String url = AUTH_SERVICE_URL + "/login?username=" + name + "&password=" + code;
                ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, null, Map.class);
                
                Map<String, Object> responseBody = responseEntity.getBody();
                if (responseBody != null) {
                    return responseBody; // 直接返回认证服务的响应
                }
            } catch (Exception e) {
                // 认证服务调用失败，返回错误信息
                result.put("code", 404);
                result.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 验证令牌
     */
    @PostMapping("/auth/verify-token")
    public Map<String, Object> verifyToken(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 临时令牌验证逻辑，仅用于测试
            if (token != null && token.startsWith("Bearer test-token-")) {
                result.put("code", 200);
                result.put("message", "令牌验证成功");
                result.put("userId", 1);
                result.put("username", "admin");
                result.put("realName", "管理员");
                result.put("userType", 2);
                result.put("avatar", "");
                return result;
            }
            
            // 如果不是测试令牌，尝试调用统一认证服务
            try {
                // 调用统一认证服务的验证令牌接口
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", token);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    AUTH_SERVICE_URL + "/verifyToken", 
                    HttpMethod.POST, 
                    entity, 
                    Map.class
                );
                
                Map<String, Object> responseBody = responseEntity.getBody();
                if (responseBody != null) {
                    return responseBody; // 直接返回认证服务的响应
                }
            } catch (Exception e) {
                // 认证服务调用失败，返回错误信息
                result.put("code", 401);
                result.put("message", "令牌验证失败");
            }
        } catch (Exception e) {
            result.put("code", 401);
            result.put("message", "令牌验证失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader("token") String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用统一认证服务的登出接口
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                AUTH_SERVICE_URL + "/logout", 
                HttpMethod.POST, 
                entity, 
                Map.class
            );
            
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null) {
                return responseBody; // 直接返回认证服务的响应
            } else {
                result.put("code", 200);
                result.put("message", "登出成功");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登出失败: " + e.getMessage());
        }
        
        return result;
    }
} 