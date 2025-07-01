package com.guangge.Interview.config;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
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
}