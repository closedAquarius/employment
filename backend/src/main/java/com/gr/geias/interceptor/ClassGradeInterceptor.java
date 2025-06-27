package com.gr.geias.interceptor;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.enums.EnableStatusEnums;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 班级权限拦截器，控制班级访问权限
 */
@Component
public class ClassGradeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        PersonInfo person = (PersonInfo) request.getSession().getAttribute("person");
        if (person.getEnableStatus() == EnableStatusEnums.ADMINISTRATOR.getState()) {
            return true;
        } else {
            String specialtyStr = request.getParameter("specialtyId");
            if (specialtyStr == null) {
                return false;
            }
            int specialtyId = Integer.parseInt(specialtyStr);
            String collegeStr = request.getParameter("collegeId");
            List<Specialty> specialties = (List<Specialty>) request.getSession().getAttribute("specialtyList");
            for (int i = 0; i < specialties.size(); i++) {
                if (specialties.get(i).getSpecialtyId() == specialtyId) {
                    if (collegeStr != null) {
                        int collegeId = Integer.parseInt(collegeStr);
                        if (person.getCollegeId() == collegeId) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
} 