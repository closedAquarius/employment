package com.guangge.Interview.config;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class FeignClientConfig {

    @Bean
    public Request.Options options() {
        // 设置连接超时为5秒，读取超时为10秒
        return new Request.Options(Duration.ofMillis(10000), Duration.ofMillis(60000),false);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 设置Feign的日志级别，帮助调试
        return Logger.Level.FULL;
    }


    @Bean
    public Retryer retryer() {
        // 设置重试策略，例如重试3次，每次间隔1秒
        return new Retryer.Default(1000, 3000, 3);
    }
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 添加一个默认的认证令牌，这里使用一个固定值作为示例
            // 在实际生产环境中，应该从安全上下文或配置中获取
            requestTemplate.header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTUxNjIzOTAyMn0.xYaUCiAiKJD-cOXH7Vhz2UMEqKQhOXhQd4piGtM0A9s");
        };
    }
}