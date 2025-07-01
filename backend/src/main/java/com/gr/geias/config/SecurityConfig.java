package com.gr.geias.config;

import com.gr.geias.interceptor.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 关闭 csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 不使用 session
                .and()
                .authorizeRequests()
                // 登录注册
                .antMatchers(
                        "/api/personinfo/login",
                        "/api/personinfo/register",
                        "/api/personinfo/refresh-token",
                        "/api/personinfo/faceLogin"
                ).permitAll()

                //welcomeController
                .antMatchers("/welcome/**").hasAnyRole("STUDENT", "TEACHER", "ADMINISTRATOR", "ENTERPRISE_HR")

                //FairController
                .antMatchers(
                        "/fairs/JobFairWithCompanies",
                        "/fairs/jobfair",
                        "/fairs/jobfairsWithboothstatus",
                        "/fairs/adminReview"
                ).hasRole("ADMINISTRATOR")
                .antMatchers(
                        "/fairs/jobfairsUnapplied",
                        "/fairs/company/jobfairs/applied",
                        "/fairs/companyApply"
                ).hasRole("ENTERPRISE_HR")

                // 其它请求需要认证
                .anyRequest().authenticated();

        // 添加 JWT 验证过滤器
        http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
