package com.gr.geias.config;

import com.gr.geias.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;
    
    @Autowired
    private AdminInterceptor adminInterceptor;
    
    @Autowired
    private SuperAdminInterceptor superAdminInterceptor;
    
    @Autowired
    private SpecialtyInterceptor specialtyInterceptor;
    
    @Autowired
    private ClassGradeInterceptor classGradeInterceptor;
    
    @Autowired
    private Person0Interceptor person0Interceptor;

    @Autowired
    private TokenInterceptor tokenInterceptor;

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/page/login", "/page/error", "/page/getinfo","/page/faseLogin")
                .excludePathPatterns("/personinfo/login", "/info/**","/personinfo/faseLogin")
                .excludePathPatterns("/html/**", "/fonts/**", "/images/**", "/js/**", "/lib/**", "/css/**");

        registry.addInterceptor(adminInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/page/index", "/page/echarts1", "/page/memberlist", "/page/echarts4", "/page/echarts2", "/page/error", "/page/login", "/page/getinfo", "/page/welcome", "/page/personedit", "/page/toexcal","/page/faseLogin")
                .excludePathPatterns("/init/getinit", "/init/getleve"
                        , "/employmentinformation/getemploymentinfo"
                        , "/employmentinformation/getcountbyemploymentway"
                        , "/employmentinformation/getcountbyunitkind"
                        , "/employmentinformation/getcountbyarea"
                        , "/personinfo/login"
                        ,"/personinfo/faseLogin"
                        , "/info/**"
                        , "/welcome/**"
                        , "/personinfo/getuser"
                        , "/personinfo/updateuser"
                        , "/employmentinformation/download"
                        ,"/personinfo/addFase")
                .excludePathPatterns("/html/**", "/fonts/**", "/images/**", "/js/**", "/lib/**", "/css/**");
                
        registry.addInterceptor(superAdminInterceptor).addPathPatterns("/page/organizationlist", "/page/collegeadd", "/page/collegeedit", "/page/person_1_list", "/page/person1add", "/page/person_1_edit")
                .addPathPatterns("/organizationcontroller/delcollege"
                        , "/organizationcontroller/getcollegelist"
                        , "/organizationcontroller/getcollegeadmin"
                        , "/organizationcontroller/addcollege"
                        , "/organizationcontroller/updatecollege"
                        , "/organizationcontroller/delperson_1"
                        , "/organizationcontroller/getperson_1"
                        , "/organizationcontroller/updateperson_1"
                        , "/organizationcontroller/addperson_1");

        registry.addInterceptor(specialtyInterceptor).addPathPatterns("/organizationcontroller/updatespecialty", "/organizationcontroller/delspecialty");
        registry.addInterceptor(classGradeInterceptor).addPathPatterns("/organizationcontroller/getclassgrade", "/organizationcontroller/addclassgrade", "/organizationcontroller/updateclassgrade", "/organizationcontroller/delclassgrade");
        registry.addInterceptor(person0Interceptor).addPathPatterns("/organizationcontroller/getpersonById", "/organizationcontroller/updateperson_0", "/organizationcontroller/delperson_0");

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**") // 拦截所有接口
                .excludePathPatterns("/api/personinfo/login", "/api/personinfo/register"); // 排除登录注册
    }

    /**
     * 配置视图解析器
     */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        return resolver;
    }

    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 配置CORS跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 添加默认视图控制器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/page/login");
        registry.addViewController("/error").setViewName("error");
    }
} 