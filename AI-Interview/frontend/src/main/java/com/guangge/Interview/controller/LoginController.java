package com.guangge.Interview.controller;

import com.guangge.Interview.auth.AuthConstant;
import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final ConsumerClient consumerClient;
    private final RestTemplate restTemplate;
    
    @Value("${consumer-service-endpoint}")
    private String consumerServiceEndpoint;

    public LoginController(ConsumerClient consumerClient, RestTemplate restTemplate) {
        this.consumerClient = consumerClient;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/login/direct")
    public CommonResult<Map<String, Object>> login(@RequestParam("name") String name,
                                            @RequestParam("code") String code) {
        logger.info("Received login request for user: {}", name);
        try {
            // 使用URL参数直接调用consumer服务
            String url = consumerServiceEndpoint + "/interview/login?name=" + name + "&code=" + code;
            
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                null, 
                Map.class
            );
            
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null) {
                return CommonResult.success(responseBody);
            } else {
                return CommonResult.failed("登录失败: 响应为空");
            }
        } catch (Exception e) {
            logger.error("Login failed: ", e);
            return CommonResult.failed("登录失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/login/verify-token")
    public CommonResult<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token) {
        logger.info("Verifying token: {}", token);
        try {
            // 添加"Bearer "前缀，与AuthConstant.TOKEN_PREFIX保持一致
            String authHeader = "Bearer " + token;
            
            // 直接使用RestTemplate调用consumer服务的/auth/verify-token端点
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                consumerServiceEndpoint + "/auth/verify-token", 
                HttpMethod.POST, 
                entity, 
                Map.class
            );
            
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null) {
                return CommonResult.success(responseBody);
            } else {
                return CommonResult.failed("令牌验证失败: 响应为空");
            }
        } catch (Exception e) {
            logger.error("Token verification failed: ", e);
            return CommonResult.failed("令牌验证失败: " + e.getMessage());
        }
    }
}
