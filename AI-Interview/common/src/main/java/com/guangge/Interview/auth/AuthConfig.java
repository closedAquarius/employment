package com.guangge.Interview.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 认证配置类，用于集成auth-service的认证服务
 */
@Configuration
public class AuthConfig {

    @Value("${auth.service.url:http://localhost:8095}")
    private String authServiceUrl;

    /**
     * 创建RestTemplate用于调用auth-service
     */
    @Bean
    public RestTemplate authServiceRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(authServiceUrl)
                .build();
    }

    /**
     * 创建AuthClient用于认证相关操作
     */
    @Bean
    public AuthClient authClient(RestTemplate authServiceRestTemplate) {
        return new AuthClient(authServiceRestTemplate);
    }
} 