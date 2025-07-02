package com.gr.geias.config;

import com.gr.geias.handler.LoggingAccessDeniedHandler;
import com.gr.geias.interceptor.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoggingAccessDeniedHandler accessDeniedHandler;

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

               //CompanyInfoController
                .antMatchers(
                        "/companyinfo/confirm-company"
                ).hasRole("ADMINISTRATOR")
                .antMatchers(
                        "/companyinfo/update-company"
                ).hasRole("ENTERPRISE_HR")

                //EmploymentInformationController
                .antMatchers(
                        "/employmentinformation/getemploymentinfo",
                        "/employmentinformation/getcountbyarea",
                        "/employmentinformation/getcountbyemploymentway",
                        "/employmentinformation/getcountbyunitkind"
                ).hasAnyRole("ADMINISTRATOR", "TEACHER")

                //FairController
                .antMatchers(
                        "/fairs/JobFairWithCompanies",
                        "/fairs/jobfair",
                        "/fairs/jobfairsWithboothstatus",
                        "/fairs/adminReview")
                .hasAnyRole("ADMINISTRATOR")
                .antMatchers(
                        "/fairs/jobfairsUnapplied",
                        "/fairs/company/jobfairs/applied",
                        "/fairs/companyApply")
                .hasAnyRole("ENTERPRISE_HR")

                //InitController
                .antMatchers("/init/getinit",
                        "init/getleve")
                .hasAnyRole("ADMINISTRATOR", "TEACHER")



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

                //welcomeController
                .antMatchers("/welcome/**").hasAnyRole("STUDENT", "TEACHER", "ADMINISTRATOR", "ENTERPRISE_HR")


                // 其它请求需要认证
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);

        // 添加 JWT 验证过滤器
        http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}