package com.gr.geias.service.impl;

import com.gr.geias.service.RouterService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouterServiceImpl implements RouterService {
    private String role;

    @Override
    public List<Map<String, Object>> getStudentRoutes() {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getTeacherRoutes() {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getAdminRoutes() {
        List<Map<String, Object>> routes = new ArrayList<>();
        routes.add(createHomeRouter());      // 首页
        routes.add(createGraduateRouter());  // 毕业生就业信息
        routes.add(createOrganizationRouter()); // 组织架构管理
        routes.add(createStaffRouter());     // 人员管理
        return routes;
    }

    @Override
    public List<Map<String, Object>> getRoutesByRole(String role) {
        this.role = role;
        switch (role) {
            case "0":
                return getStudentRoutes();
            case "1":
                return getTeacherRoutes();
            case "2":
                return getAdminRoutes();
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 创建首页路由
     */
    private Map<String, Object> createHomeRouter() {
        Map<String, Object> homeRouter = new HashMap<>();
        homeRouter.put("path", "/adminWelcome");
        if (role.equals("2")) {
            homeRouter.put("name", "AdminWelcomePage");
        }
        homeRouter.put("redirect", "/welcome");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "ep/home-filled");
        meta.put("title", "首页");
        meta.put("rank", 1);
        homeRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();
        Map<String, Object> welcomeChild = new HashMap<>();
        if (role.equals("2")) {
            welcomeChild.put("path", "/adminWelcome/index");
            welcomeChild.put("name", "AdminWelcome");
            welcomeChild.put("component", "adminWelcome/index");
        }

        Map<String, Object> welcomeMeta = new HashMap<>();
        welcomeMeta.put("title", "首页");
        welcomeChild.put("meta", welcomeMeta);

        children.add(welcomeChild);
        homeRouter.put("children", children);

        return homeRouter;
    }

    /**
     * 创建毕业生就业信息路由
     */
    private Map<String, Object> createGraduateRouter() {
        Map<String, Object> graduateRouter = new HashMap<>();
        graduateRouter.put("path", "/graduate");
        graduateRouter.put("name", "Graduate");
        graduateRouter.put("redirect", "/graduate");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/graduate");
        meta.put("title", "毕业生就业信息");
        meta.put("rank", 2);
        graduateRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 毕业生就业总体数据
        Map<String, Object> dataChild = new HashMap<>();
        dataChild.put("path", "/graduate/data");
        dataChild.put("name", "GraduateData");
        dataChild.put("component", "graduate/data");
        Map<String, Object> dataMeta = new HashMap<>();
        dataMeta.put("title", "毕业生就业总体数据");
        dataChild.put("meta", dataMeta);
        children.add(dataChild);

        // 毕业生主要就业方式
        Map<String, Object> typeChild = new HashMap<>();
        typeChild.put("path", "/graduate/type");
        typeChild.put("name", "GraduateType");
        typeChild.put("component", "graduate/type");
        Map<String, Object> typeMeta = new HashMap<>();
        typeMeta.put("title", "毕业生主要就业方式");
        typeChild.put("meta", typeMeta);
        children.add(typeChild);

        // 毕业生就业工作性质
        Map<String, Object> attributeChild = new HashMap<>();
        attributeChild.put("path", "/graduate/attribute");
        attributeChild.put("name", "GraduateAttribut");
        attributeChild.put("component", "graduate/attribute");
        Map<String, Object> attributeMeta = new HashMap<>();
        attributeMeta.put("title", "毕业生就业工作性质");
        attributeChild.put("meta", attributeMeta);
        children.add(attributeChild);

        // 毕业生主要就业位置
        Map<String, Object> locationChild = new HashMap<>();
        locationChild.put("path", "/graduate/location");
        locationChild.put("name", "GraduateLocation");
        locationChild.put("component", "graduate/location");
        Map<String, Object> locationMeta = new HashMap<>();
        locationMeta.put("title", "毕业生主要就业位置");
        locationChild.put("meta", locationMeta);
        children.add(locationChild);

        graduateRouter.put("children", children);
        return graduateRouter;
    }

    /**
     * 创建组织架构管理路由
     */
    private Map<String, Object> createOrganizationRouter() {
        Map<String, Object> organizationRouter = new HashMap<>();
        organizationRouter.put("path", "/organization");
        organizationRouter.put("redirect", "/organization/403");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/organization");
        meta.put("title", "组织架构管理");
        meta.put("rank", 3);
        organizationRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 组织架构图
        Map<String, Object> pictureChild = new HashMap<>();
        pictureChild.put("path", "/organization/picture");
        pictureChild.put("name", "picture");
        pictureChild.put("component", "organization/picture");
        Map<String, Object> pictureMeta = new HashMap<>();
        pictureMeta.put("title", "组织架构图");
        pictureChild.put("meta", pictureMeta);
        children.add(pictureChild);

        // 学院信息管理
        Map<String, Object> collegeChild = new HashMap<>();
        collegeChild.put("path", "/organization/college");
        collegeChild.put("name", "college");
        collegeChild.put("component", "organization/college");
        Map<String, Object> collegeMeta = new HashMap<>();
        collegeMeta.put("title", "学院信息管理");
        collegeChild.put("meta", collegeMeta);
        children.add(collegeChild);

        // 专业信息管理
        Map<String, Object> majorChild = new HashMap<>();
        majorChild.put("path", "/organization/major");
        majorChild.put("name", "major");
        majorChild.put("component", "organization/major");
        Map<String, Object> majorMeta = new HashMap<>();
        majorMeta.put("title", "专业信息管理");
        majorChild.put("meta", majorMeta);
        children.add(majorChild);

        // 班级信息管理
        Map<String, Object> classChild = new HashMap<>();
        classChild.put("path", "/organization/class");
        classChild.put("name", "class");
        classChild.put("component", "organization/class");
        Map<String, Object> classMeta = new HashMap<>();
        classMeta.put("title", "班级信息管理");
        classChild.put("meta", classMeta);
        children.add(classChild);

        organizationRouter.put("children", children);
        return organizationRouter;
    }

    /**
     * 创建人员管理路由
     */
    private Map<String, Object> createStaffRouter() {
        Map<String, Object> staffRouter = new HashMap<>();
        staffRouter.put("path", "/staff");
        staffRouter.put("redirect", "/staff/counselor");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/person");
        meta.put("title", "人员管理");
        meta.put("rank", 4);
        staffRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 辅导员管理
        Map<String, Object> counselorChild = new HashMap<>();
        counselorChild.put("path", "/staff/counselor");
        counselorChild.put("name", "counselor");
        counselorChild.put("component", "staff/counselor");
        Map<String, Object> counselorMeta = new HashMap<>();
        counselorMeta.put("title", "辅导员管理");
        counselorChild.put("meta", counselorMeta);
        children.add(counselorChild);

        // 学院管理员管理
        Map<String, Object> administratorChild = new HashMap<>();
        administratorChild.put("path", "/staff/administrator");
        administratorChild.put("name", "administrator");
        administratorChild.put("component", "staff/administrator");
        Map<String, Object> administratorMeta = new HashMap<>();
        administratorMeta.put("title", "学院管理员管理");
        administratorChild.put("meta", administratorMeta);
        children.add(administratorChild);

        staffRouter.put("children", children);
        return staffRouter;
    }
}
