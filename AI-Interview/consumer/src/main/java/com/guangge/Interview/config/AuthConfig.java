package com.guangge.Interview.config;

import com.guangge.Interview.auth.AuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthConfig {

    @Value("${auth.service.url:http://localhost:8095}")
    private String authServiceUrl;
    
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public AuthClient authClient(RestTemplate restTemplate) {
        return new AuthClient(restTemplate, authServiceUrl);
    }
} 