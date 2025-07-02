package com.guangge.Interview.config;

import com.guangge.Interview.auth.AuthClient;
import com.guangge.Interview.auth.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${auth.service.url:http://localhost:8095}")
    private String authServiceUrl;
    
    @Value("${jwt.secret:eAf6XIz7Q6CmE3N4K5L6M7N8O9P0Q1R2S3T4U5V6W7X8Y9Z0aBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz}")
    private String jwtSecret;

    @Autowired
    private AuthClient authClient;

    /**
     * 配置CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加认证拦截器，排除登录、健康检查等公开接口
        registry.addInterceptor(authInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login/**",
                        "/auth/**",
                        "/error",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/interview/welcomemp3",
                        "/interview/login",
                        "/candidates",
                        "/frontend/candidates",
                        "/frontend/interView",
                        "/frontend/sendMail",
                        "/interview/face2faceChat",
                        "/interview/makeProgram",
                        "/interview/checkProgram",
                        "/interview/chat"
                );
    }

    /**
     * 创建认证拦截器
     */
    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor(authClient);
    }
    
    /**
     * 创建AuthClient Bean，用于验证JWT令牌
     */
    @Bean
    public AuthClient authClient(RestTemplate restTemplate) {
        return new AuthClient(restTemplate, authServiceUrl, jwtSecret);
    }

    /**
     * 创建RestTemplate Bean，用于调用统一认证服务
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 