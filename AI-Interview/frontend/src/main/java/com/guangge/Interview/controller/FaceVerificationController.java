package com.guangge.Interview.controller;

import com.guangge.Interview.vo.RegisterFaceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;

@RestController
@RequestMapping("/api")
public class FaceVerificationController {
    private static final Logger logger = LoggerFactory.getLogger(FaceVerificationController.class);

    @Value("${flaskBaseUrl}")
    private String flaskBaseUrl;

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
