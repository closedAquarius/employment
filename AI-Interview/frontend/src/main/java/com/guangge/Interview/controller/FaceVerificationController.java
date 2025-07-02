package com.guangge.Interview.controller;

import com.guangge.Interview.auth.AuthClient;
import com.guangge.Interview.auth.AuthConstant;
import com.guangge.Interview.vo.RegisterFaceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FaceVerificationController {
    private static final Logger logger = LoggerFactory.getLogger(FaceVerificationController.class);

    @Value("${face-service.url}")
    private String flaskBaseUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private AuthClient authClient;

    @PostMapping("/verify")
    public Map<String, Object> verifyFace(@RequestBody Map<String, Object> request, 
                                         @RequestHeader(value = AuthConstant.HEADER_AUTH, required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证令牌并获取用户信息
            Map<String, Object> userInfo = null;
            if (authHeader != null && authHeader.startsWith(AuthConstant.TOKEN_PREFIX)) {
                String token = authHeader.substring(AuthConstant.TOKEN_PREFIX.length()).trim();
                logger.info("Validating token for face verification");
                userInfo = authClient.validateToken(token);
                logger.info("Token validation result: {}", userInfo != null ? "success" : "failed");
            } else {
                logger.warn("No valid auth header provided for face verification");
            }
            
            // 如果没有有效的用户信息，使用请求中的用户ID
            String userId = request.containsKey("userId") ? request.get("userId").toString() : 
                           (userInfo != null ? userInfo.get("id").toString() : "unknown");
            
            logger.info("Processing face verification for userId: {}", userId);
            
            // 准备请求数据
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("image", request.get("image"));
            requestData.put("userId", userId);
            
            // 调用Flask服务进行人脸验证
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            
            logger.info("Sending request to face service: {}/verify", flaskBaseUrl);
            ResponseEntity<Map> result = restTemplate.postForEntity(
                    flaskBaseUrl + "/verify", 
                    entity, 
                    Map.class
            );
            logger.info("Face service response status: {}", result.getStatusCode());
            
            // 如果是新用户且验证成功，同步用户信息到认证服务
            if (result.getBody() != null && 
                Boolean.TRUE.equals(result.getBody().get("success")) && 
                Boolean.TRUE.equals(result.getBody().get("isNewUser")) &&
                userInfo == null) {
                
                // 创建基本用户信息
                Map<String, Object> newUser = new HashMap<>();
                newUser.put("username", "face_" + userId);
                newUser.put("password", "face_" + userId); // 初始密码，应当要求用户修改
                newUser.put("userType", AuthConstant.UserType.INTERVIEWEE);
                newUser.put("status", AuthConstant.UserStatus.ENABLED);
                
                // 同步到认证服务
                logger.info("Syncing new user to auth service: {}", userId);
                authClient.syncUser(newUser);
            }
            
            return result.getBody();
        } catch (Exception e) {
            logger.error("Face verification error", e);
            response.put("success", false);
            response.put("message", "人脸验证服务异常: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/health")
    public Map<String, String> checkHealth() {
        Map<String, String> response = new HashMap<>();
        try {
            ResponseEntity<Map> result = restTemplate.getForEntity(flaskBaseUrl + "/health", Map.class);
            if (result.getStatusCode().is2xxSuccessful()) {
                response.put("status", "UP");
                response.put("message", "Face verification service is running");
            } else {
                response.put("status", "DOWN");
                response.put("message", "Face verification service returned non-200 status");
            }
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("message", "Cannot connect to face verification service: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/verify-face")
    public String verifyFace(@RequestBody RegisterFaceRequest request) {
        try {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"image\": \"" + request.getImage() + "\", \"userId\": \"" + request.getUserId() + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            logger.info("Sending face verification request to: {}/verify-face for user: {}", flaskBaseUrl, request.getUserId());
        ResponseEntity<String> response = restTemplate.postForEntity(flaskBaseUrl + "/verify-face", requestEntity, String.class);
            logger.info("Face verification response status: {}", response.getStatusCode());
        return response.getBody();
        } catch (HttpStatusCodeException e) {
            // 捕获HTTP状态码异常，但仍然返回响应体
            logger.error("Face verification failed with status code {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("Face verification failed with exception", e);
            return "{\"status\": \"1\", \"message\": \"Face verification service error: " + e.getMessage() + "\"}";
        }
    }

    @PostMapping("/register-face")
    public String registerFace(@RequestBody RegisterFaceRequest request) {
        try {
            logger.info("Registering face for user: {}", request.getUserId());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"image\": \"" + request.getImage() + "\", \"userId\": \"" + request.getUserId() + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskBaseUrl + "/register-face", requestEntity, String.class);
            logger.info("Face registration response status: {}", response.getStatusCode());
        return response.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("Face registration failed with status code {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("Face registration failed with exception", e);
            return "{\"status\": \"1\", \"message\": \"Face registration service error: " + e.getMessage() + "\"}";
        }
    }
}
