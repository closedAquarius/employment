package com.gr.geias.service;

import java.util.List;
import java.util.Map;

public interface RouterService {
    List<Map<String, Object>> getStudentRoutes();
    List<Map<String, Object>> getTeacherRoutes();
    List<Map<String, Object>> getAdminRoutes();
    List<Map<String, Object>> getRoutesByRole(String role);
    public List<Map<String, Object>> getHRRoutes();
}
