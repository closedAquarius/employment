package com.guangge.Interview.config;

import com.guangge.Interview.auth.AuthClient;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FeignClientConfig {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FeignClientConfig.class);
    private final AuthClient authClient;

    public FeignClientConfig(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Bean
    public Request.Options options() {
        // 设置连接超时为10秒，读取超时为60秒
        log.info("Configuring Feign client with connection timeout: 10s, read timeout: 60s");
        return new Request.Options(10000, 60000, false);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 设置Feign的日志级别，帮助调试
        log.info("Setting Feign logger level to FULL");
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        // 设置重试策略，例如重试3次，每次间隔1秒
        log.info("Configuring Feign retryer: 3 attempts, 1s initial delay, 3s max delay");
        return new Retryer.Default(1000, 3000, 3);
    }
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            log.debug("Adding authorization header to request: {}", requestTemplate.path());
            requestTemplate.header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTUxNjIzOTAyMn0.xYaUCiAiKJD-cOXH7Vhz2UMEqKQhOXhQd4piGtM0A9s");
        };
    }
    
    @Bean
    @Primary
    public SpringFormEncoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        log.info("Creating SpringFormEncoder for Feign multipart support");
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
        log.info("Creating StandardServletMultipartResolver");
        return new StandardServletMultipartResolver();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default() {
            @Override
            public Exception decode(String methodKey, feign.Response response) {
                log.error("Feign error: {} - Status: {}, Headers: {}", 
                         methodKey, response.status(), response.headers());
                
                try {
                    // 尝试读取响应体
                    if (response.body() != null) {
                        byte[] bodyData = feign.Util.toByteArray(response.body().asInputStream());
                        String responseBody = new String(bodyData);
                        log.error("Response body: {}", responseBody);
                    }
                } catch (Exception e) {
                    log.error("Failed to read response body", e);
                }
                
                return super.decode(methodKey, response);
            }
        };
    }
    
    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringDecoder(messageConverters);
    }
    
    @Bean
    public HttpMessageConverters customConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        
        // 添加ByteArrayHttpMessageConverter，支持audio/wav等二进制响应
        ByteArrayHttpMessageConverter byteArrayConverter = new ByteArrayHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>(byteArrayConverter.getSupportedMediaTypes());
        supportedMediaTypes.add(MediaType.valueOf("audio/wav"));
        supportedMediaTypes.add(MediaType.valueOf("audio/mp3"));
        byteArrayConverter.setSupportedMediaTypes(supportedMediaTypes);
        
        converters.add(byteArrayConverter);
        
        // 添加JSON转换器
        converters.add(new MappingJackson2HttpMessageConverter());
        
        log.info("Configured custom HttpMessageConverters with audio/wav support");
        return new HttpMessageConverters(converters);
    }
}