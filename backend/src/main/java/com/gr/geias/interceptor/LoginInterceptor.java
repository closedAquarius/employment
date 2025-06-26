package com.gr.geias.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器，未登录用户重定向到登录页面
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 拦截登录，没用登录的一律返回登录页
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return 是否通过拦截
     * @throws Exception 可能的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object person = request.getSession().getAttribute("person");
        if (person != null) {
            return true;
        } else {
            response.sendRedirect("/page/login");
            return false;
        }
    }
} 