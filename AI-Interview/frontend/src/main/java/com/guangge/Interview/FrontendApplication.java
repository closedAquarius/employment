package com.guangge.Interview;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.reactive.function.client.WebClient;
import com.guangge.Interview.auth.AuthConfig;
import jakarta.servlet.MultipartConfigElement;

@SpringBootApplication
@Theme(value = "customer-support-agent")
@EnableFeignClients
@ComponentScan(basePackages = "com.guangge.Interview", 
               excludeFilters = @ComponentScan.Filter(
                   type = FilterType.ASSIGNABLE_TYPE, 
                   classes = {AuthConfig.class}
               ))
public class FrontendApplication extends SpringBootServletInitializer implements AppShellConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(FrontendApplication.class);

    public static void main(String[] args) {
        logger.info("Starting AI-Interview Frontend Application");
        SpringApplication.run(FrontendApplication.class, args);
        logger.info("AI-Interview Frontend Application started successfully");
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        return resolver;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        return factory.createMultipartConfig();
    }
}