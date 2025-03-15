package com.guangge.Interview;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@Theme(value = "customer-support-agent")
@EnableFeignClients
public class FrontendApplication implements AppShellConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(FrontendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

}