package com.guangge.Interview.config;

import com.guangge.Interview.auth.AuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${auth.service.url:http://localhost:8095}")
    private String authServiceUrl;
    
    @Value("${consumer-service-endpoint}")
    private String consumerServiceEndpoint;
    
    @Value("${python.azure.openai.service.url:http://localhost:7860}")
    private String pythonServiceUrl;
    
    // 图片目录配置，默认为项目根目录下的external/static/images
    @Value("${app.image.directory:#{null}}")
    private String imageDirectory;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(pythonServiceUrl)
                .build();
    }
    
    @Bean
    public AuthClient authClient(RestTemplate restTemplate) {
        return new AuthClient(restTemplate, authServiceUrl);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "X-Chat-Status", "Content-Type", "Content-Disposition", "Cache-Control")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("配置静态资源映射...");
        
        // 如果没有配置外部目录，使用默认目录
        String externalImagesDir = imageDirectory;
        if (externalImagesDir == null) {
            externalImagesDir = System.getProperty("user.dir") + File.separator + 
                    "external" + File.separator + "static" + File.separator + "images";
        }
        
        File imagesDir = new File(externalImagesDir);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
            System.out.println("创建图片目录: " + imagesDir.getAbsolutePath());
        }
        
        // 外部文件系统路径图片映射（优先级更高）
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + externalImagesDir + File.separator);
                
        System.out.println("图片目录映射: /images/** -> " + "file:" + externalImagesDir + File.separator);
        
        // 类路径图片映射（作为备选）
        registry.addResourceHandler("/static-images/**")
                .addResourceLocations("classpath:/static/images/");
        
        // 添加其他静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
} 