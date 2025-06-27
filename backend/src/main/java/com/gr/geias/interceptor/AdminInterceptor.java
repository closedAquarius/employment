package com.gr.geias.interceptor;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.enums.EnableStatusEnums;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员权限拦截器，只允许校长和院长访问
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        PersonInfo person = (PersonInfo)request.getSession().getAttribute("person");
        if (person.getEnableStatus() == EnableStatusEnums.ADMINISTRATOR.getState()) {
            return true;
        }
        if (person.getEnableStatus() == EnableStatusEnums.PREXY.getState()) {
            return true;
        }
        response.sendRedirect("/page/error");
        return false;
    }
} 