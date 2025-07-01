package com.gr.geias.interceptor;

import com.gr.geias.model.CompanyInfo;
import com.gr.geias.service.CompanyInfoService;
import com.gr.geias.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CompanyUserInterceptor implements HandlerInterceptor {

    @Autowired
    private CompanyInfoService companyInfoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少token");
            return false;
        }

        try {
            Claims claims = JwtUtil.parseAccessToken(token);
            Integer enableStatus = (Integer) claims.get("roles");
            Integer personId = (Integer) claims.get("userId");

            // 如果是企业人员
            if (enableStatus == 2) {
                CompanyInfo company = companyInfoService.getCompanyByPersonId(personId);
                if (company == null || Boolean.FALSE.equals(company.getConfirmed())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "企业信息未确认，禁止操作");
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token无效或已过期");
            return false;
        }
    }
}
