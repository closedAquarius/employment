package com.guangge.Interview.controller;

import com.guangge.Interview.vo.RegisterFaceRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
public class FaceVerificationController {

    @Value("${flaskBaseUrl}")
    private String flaskBaseUrl;

    @PostMapping("/verify-face")
    public String verifyFace(@RequestBody RegisterFaceRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"image\": \"" + request.getImage() + "\", \"userId\": \"" + request.getUserId() + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskBaseUrl + "/verify-face", requestEntity, String.class);
        return response.getBody();
    }

    @PostMapping("/register-face")
    public String registerFace(@RequestBody RegisterFaceRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"image\": \"" + request.getImage() + "\", \"userId\": \"" + request.getUserId() + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskBaseUrl + "/register-face", requestEntity, String.class);
        return response.getBody();
    }
}
