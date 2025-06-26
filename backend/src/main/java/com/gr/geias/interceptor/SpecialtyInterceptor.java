package com.gr.geias.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.enums.EnableStatusEnums;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 专业访问权限拦截器
 */
@Component
public class SpecialtyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean collegeIsOK = false;
        boolean specialtyIsOK = false;
        PersonInfo person = (PersonInfo) request.getSession().getAttribute("person");
        if (person.getEnableStatus() == EnableStatusEnums.schoolmaster.getState()) {
            return true;
        }
        if (person.getEnableStatus() == EnableStatusEnums.PREXY.getState()) {
            String collegeStr = request.getParameter("collegeId");
            if (collegeStr != null) {
                int collegeId = Integer.parseInt(collegeStr);
                if (collegeId == person.getCollegeId()) {
                    collegeIsOK = true;
                } else {
                    collegeIsOK = false;
                }
            } else {
                collegeIsOK = true;
            }
            String specialtyStr = request.getParameter("specialtyId");
            if (specialtyStr == null) {
                specialtyIsOK = false;
            } else {
                int specialtyId = Integer.parseInt(specialtyStr);
                List<Specialty> specialtyList = (List<Specialty>) request.getSession().getAttribute("specialtyList");
                if (specialtyList == null) {
                    specialtyIsOK = false;
                } else {
                    for (int i = 0; i < specialtyList.size(); i++) {
                        if (specialtyList.get(i).getSpecialtyId().equals(specialtyId)) {
                            specialtyIsOK = true;
                            break;
                        }
                    }
                }
            }
        }
        return (specialtyIsOK && collegeIsOK);
    }
} 