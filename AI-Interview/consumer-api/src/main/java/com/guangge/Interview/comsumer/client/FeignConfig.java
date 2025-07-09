package com.guangge.Interview.comsumer.client;

import feign.codec.Encoder;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonEncoder;
import feign.jackson.JacksonDecoder;
import feign.Logger;
import feign.Request;
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
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FeignConfig.class);

    @Bean
    @Primary  // 标记为主要编码器
    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        log.info("Creating SpringFormEncoder for Feign multipart support");
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
    
    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringDecoder(messageConverters);
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        log.info("Configuring StandardServletMultipartResolver for Feign");
        return resolver;
    }
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        // 设置Feign的日志级别为FULL，以便调试
        return Logger.Level.FULL;
    }
    
    @Bean
    public Request.Options options() {
        // 设置连接超时为10秒，读取超时为60秒
        return new Request.Options(
            10, TimeUnit.SECONDS, // 连接超时
            60, TimeUnit.SECONDS, // 读取超时
            true // 跟随重定向
        );
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
    public HttpMessageConverters customConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        
        // 添加ByteArrayHttpMessageConverter，支持audio/wav等二进制响应
        ByteArrayHttpMessageConverter byteArrayConverter = new ByteArrayHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>(byteArrayConverter.getSupportedMediaTypes());
        supportedMediaTypes.add(MediaType.valueOf("audio/wav"));
        supportedMediaTypes.add(MediaType.valueOf("audio/mp3"));
        byteArrayConverter.setSupportedMediaTypes(supportedMediaTypes);
        
        converters.add(byteArrayConverter);
        
        // 添加ResourceHttpMessageConverter
        ResourceHttpMessageConverter resourceConverter = new ResourceHttpMessageConverter();
        converters.add(resourceConverter);
        
        // 添加自定义的ResponseEntityHttpMessageConverter
        ResponseEntityHttpMessageConverter responseEntityConverter = new ResponseEntityHttpMessageConverter();
        converters.add(responseEntityConverter);
        
        // 添加JSON转换器
        converters.add(new MappingJackson2HttpMessageConverter());
        
        log.info("Configured custom HttpMessageConverters with audio/wav support");
        return new HttpMessageConverters(converters);
    }
}
