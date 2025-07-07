package com.gr.geias.service.impl;

import com.gr.geias.service.RouterService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouterServiceImpl implements RouterService {
    private String role;

    @Override
    public List<Map<String, Object>> getStudentRoutes() {
        List<Map<String, Object>> routes = new ArrayList<>();
        routes.add(createCVRouter());      // 个人简历
        routes.add(createAttendJobFairRouter());  // 招聘宣讲
        routes.add(createInterviewRouter()); // 模拟面试
        routes.add(createMessageRouter());  // 消息管理
        return routes;
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
        routes.add(createAdminJobAndPresentationRouter());       // 宣讲会和招聘会管理
        routes.add(createScreenRouter());   // 数字大屏
        routes.add(createMessageRouter());  // 消息管理
        return routes;
    }

    @Override
    public List<Map<String, Object>> getHRRoutes() {
        List<Map<String, Object>> routes = new ArrayList<>();
        routes.add(createCompanyInfoRouter());      // 企业信息首页
        routes.add(createJobFairRouter());  // 申请宣讲招聘会
        routes.add(createJobRouter()); // 发布岗位
        routes.add(createMessageRouter());  // 消息管理
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
            case "3":
                return getHRRoutes();
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
        /*
        if (role.equals("2")) {
            homeRouter.put("name", "AdminWelcomePage");
        }*/
        homeRouter.put("redirect", "/adminWelcome/index");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/myHome");
        meta.put("title", "我的桌面");
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
        welcomeMeta.put("title", "我的桌面");
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

        // 学生管理
        Map<String, Object> studentChild = new HashMap<>();
        studentChild.put("path", "/staff/student");
        studentChild.put("name", "studentManage");
        studentChild.put("component", "staff/student");
        Map<String, Object> studentMeta = new HashMap<>();
        studentMeta.put("title", "学生管理");
        studentChild.put("meta", studentMeta);
        children.add(studentChild);

        staffRouter.put("children", children);
        return staffRouter;
    }

    private Map<String, Object> createAdminJobAndPresentationRouter() {
        Map<String, Object> graduateRouter = new HashMap<>();
        graduateRouter.put("path", "/publishJobFair");
        graduateRouter.put("name", "publishJobFair");
        graduateRouter.put("redirect", "/publishJobFair");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/jobFair");
        meta.put("title", "宣讲与招聘");
        meta.put("rank", 5);
        graduateRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 毕业生就业总体数据
        Map<String, Object> dataChild = new HashMap<>();
        dataChild.put("path", "/publishJobFair/presentation");
        dataChild.put("name", "publishJobFairOfPresentation");
        dataChild.put("component", "publishJobFair/presentation");
        Map<String, Object> dataMeta = new HashMap<>();
        dataMeta.put("title", "宣讲会");
        dataChild.put("meta", dataMeta);
        children.add(dataChild);

        // 毕业生主要就业方式
        Map<String, Object> typeChild = new HashMap<>();
        typeChild.put("path", "/publishJobFair/jobFair");
        typeChild.put("name", "publishJobFairOfJobFair");
        typeChild.put("component", "publishJobFair/jobFair");
        Map<String, Object> typeMeta = new HashMap<>();
        typeMeta.put("title", "招聘会");
        typeChild.put("meta", typeMeta);
        children.add(typeChild);

        graduateRouter.put("children", children);
        return graduateRouter;
    }

    /**
     * 创建数字大屏路由
     */
    private Map<String, Object> createScreenRouter() {
        Map<String, Object> homeRouter = new HashMap<>();
        homeRouter.put("path", "/screen");
        homeRouter.put("name", "DigitalPage");
        homeRouter.put("redirect", "/screen/index");

        Map<String, Object> meta = new HashMap<>();
        // meta.put("icon", "custom/screen");
        meta.put("title", "数字大屏");
        meta.put("rank", 7);
        homeRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();
        Map<String, Object> welcomeChild = new HashMap<>();
        if (role.equals("2")) {
            welcomeChild.put("path", "/screen/index");
            welcomeChild.put("name", "screen");
            welcomeChild.put("component", "screen/index");
        }

        Map<String, Object> welcomeMeta = new HashMap<>();
        welcomeMeta.put("title", "数字大屏");
        welcomeChild.put("meta", welcomeMeta);

        children.add(welcomeChild);
        homeRouter.put("children", children);

        return homeRouter;
    }

    /**
     * 创建消息中心路由
     */
    private Map<String, Object> createMessageRouter() {
        Map<String, Object> homeRouter = new HashMap<>();
        homeRouter.put("path", "/message");
        homeRouter.put("name", "MessagePage");
        homeRouter.put("redirect", "/message/index");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/message");
        meta.put("title", "消息中心");
        meta.put("rank", 8);
        homeRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();
        Map<String, Object> welcomeChild = new HashMap<>();
        welcomeChild.put("path", "/message/index");
        welcomeChild.put("name", "message");
        welcomeChild.put("component", "message/index");

        Map<String, Object> welcomeMeta = new HashMap<>();
        welcomeMeta.put("title", "消息中心");
        welcomeChild.put("meta", welcomeMeta);

        children.add(welcomeChild);
        homeRouter.put("children", children);

        return homeRouter;
    }

    /**
     * 创建学生个人简历路由
     */
    private Map<String, Object> createCVRouter() {
        Map<String, Object> cvRouter = new HashMap<>();
        cvRouter.put("path", "/studentCV");
        cvRouter.put("redirect", "/studentCV/newCV");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/jobFair");
        meta.put("title", "个人简历");
        meta.put("rank", 1);
        cvRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 创建个人简历
        Map<String, Object> newChild = new HashMap<>();
        newChild.put("path", "/studentCV/newCV");
        newChild.put("name", "newCV");
        newChild.put("component", "studentCV/newCV");
        Map<String, Object> newMeta = new HashMap<>();
        newMeta.put("title", "创建简历");
        newChild.put("meta", newMeta);
        children.add(newChild);

        // 优化个人简历
        Map<String, Object> modifyChild = new HashMap<>();
        modifyChild.put("path", "/studentCV/modifyCV");
        modifyChild.put("name", "modifyCV");
        modifyChild.put("component", "studentCV/modifyCV");
        Map<String, Object> modifyMeta = new HashMap<>();
        modifyMeta.put("title", "优化简历");
        modifyChild.put("meta", modifyMeta);
        children.add(modifyChild);

        cvRouter.put("children", children);
        return cvRouter;
    }

    /**
     * 创建学生参与招聘宣讲路由
     */
    private Map<String, Object> createAttendJobFairRouter() {
        Map<String, Object> staffRouter = new HashMap<>();
        staffRouter.put("path", "/attendJobFair");
        staffRouter.put("redirect", "/attendJobFair/presentation");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/jobFair");
        meta.put("title", "宣讲招聘");
        meta.put("rank", 4);
        staffRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 参加宣讲会管理
        Map<String, Object> presentationChild = new HashMap<>();
        presentationChild.put("path", "/attendJobFair/presentation");
        presentationChild.put("name", "presentation");
        presentationChild.put("component", "attendJobFair/presentation");
        Map<String, Object> counselorMeta = new HashMap<>();
        counselorMeta.put("title", "宣讲会");
        presentationChild.put("meta", counselorMeta);
        children.add(presentationChild);

        // 参加招聘会管理
        Map<String, Object> jobFairChild = new HashMap<>();
        jobFairChild.put("path", "/attendJobFair/jobFair");
        jobFairChild.put("name", "jobFair");
        jobFairChild.put("component", "attendJobFair/jobFair");
        Map<String, Object> administratorMeta = new HashMap<>();
        administratorMeta.put("title", "招聘会");
        jobFairChild.put("meta", administratorMeta);
        children.add(jobFairChild);

        staffRouter.put("children", children);
        return staffRouter;
    }

    /**
     * 创建模拟面试路由
     */
    private Map<String, Object> createInterviewRouter() {
        Map<String, Object> organizationRouter = new HashMap<>();
        organizationRouter.put("path", "/interview");
        organizationRouter.put("redirect", "/interview/match");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/interview");
        meta.put("title", "模拟面试");
        meta.put("rank", 3);
        organizationRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 职能匹配
        Map<String, Object> matchChild = new HashMap<>();
        matchChild.put("path", "/interview/match");
        matchChild.put("name", "match");
        matchChild.put("component", "interview/match");
        Map<String, Object> matchMeta = new HashMap<>();
        matchMeta.put("title", "职能匹配");
        matchChild.put("meta", matchMeta);
        children.add(matchChild);

        // AI面试
        Map<String, Object> writeChild = new HashMap<>();
        writeChild.put("path", "/interview/write");
        writeChild.put("name", "write");
        writeChild.put("component", "interview/write");
        Map<String, Object> writeMeta = new HashMap<>();
        writeMeta.put("title", "AI笔试");
        writeChild.put("meta", writeMeta);
        children.add(writeChild);

        // AI面试
        Map<String, Object> aiChild = new HashMap<>();
        aiChild.put("path", "/interview/ai");
        aiChild.put("name", "ai");
        aiChild.put("component", "interview/ai");
        Map<String, Object> aiMeta = new HashMap<>();
        aiMeta.put("title", "AI面试");
        aiChild.put("meta", aiMeta);
        children.add(aiChild);

        // 老板面试
        Map<String, Object> bossChild = new HashMap<>();
        bossChild.put("path", "/interview/boss");
        bossChild.put("name", "boss");
        bossChild.put("component", "interview/boss");
        Map<String, Object> bossMeta = new HashMap<>();
        bossMeta.put("title", "老板面试");
        bossChild.put("meta", bossMeta);
        children.add(bossChild);

        // 面试结果
        Map<String, Object> resultChild = new HashMap<>();
        resultChild.put("path", "/interview/result");
        resultChild.put("name", "result");
        resultChild.put("component", "interview/result");
        Map<String, Object> resultMeta = new HashMap<>();
        resultMeta.put("title", "面试结果");
        resultChild.put("meta", resultMeta);
        children.add(resultChild);

        organizationRouter.put("children", children);
        return organizationRouter;
    }

    /**
     * 创建企业信息路由
     */
    private Map<String, Object> createCompanyInfoRouter() {
        Map<String, Object> homeRouter = new HashMap<>();
        homeRouter.put("path", "/companyInfo");
        homeRouter.put("redirect", "/companyInfo/index");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/sv");
        meta.put("title", "企业信息");
        meta.put("rank", 1);
        homeRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();
        Map<String, Object> welcomeChild = new HashMap<>();
        welcomeChild.put("path", "/companyInfo/index");
        welcomeChild.put("name", "CompanyInfo");
        welcomeChild.put("component", "companyInfo/index");


        Map<String, Object> welcomeMeta = new HashMap<>();
        welcomeMeta.put("title", "企业信息");
        welcomeChild.put("meta", welcomeMeta);

        children.add(welcomeChild);
        homeRouter.put("children", children);

        return homeRouter;
    }

    /**
     * 创建招聘发布路由
     */
    private Map<String, Object> createJobFairRouter() {
        Map<String, Object> staffRouter = new HashMap<>();
        staffRouter.put("path", "/holdJobFair");
        staffRouter.put("redirect", "/holdJobFair/presentation");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/jobFair");
        meta.put("title", "宣讲招聘");
        meta.put("rank", 4);
        staffRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();

        // 参加宣讲会管理
        Map<String, Object> presentationChild = new HashMap<>();
        presentationChild.put("path", "/holdJobFair/presentation");
        presentationChild.put("name", "holdPresentation");
        presentationChild.put("component", "holdJobFair/presentation");
        Map<String, Object> counselorMeta = new HashMap<>();
        counselorMeta.put("title", "申请宣讲会");
        presentationChild.put("meta", counselorMeta);
        children.add(presentationChild);

        // 参加招聘会管理
        Map<String, Object> jobFairChild = new HashMap<>();
        jobFairChild.put("path", "/holdJobFair/jobFair");
        jobFairChild.put("name", "holdJobFair");
        jobFairChild.put("component", "holdJobFair/jobFair");
        Map<String, Object> administratorMeta = new HashMap<>();
        administratorMeta.put("title", "发布招聘会");
        jobFairChild.put("meta", administratorMeta);
        children.add(jobFairChild);

        staffRouter.put("children", children);
        return staffRouter;
    }

    /**
     * 创建发布岗位路由
     */
    private Map<String, Object> createJobRouter() {
        Map<String, Object> homeRouter = new HashMap<>();
        homeRouter.put("path", "/job");
        homeRouter.put("redirect", "/job/index");

        Map<String, Object> meta = new HashMap<>();
        meta.put("icon", "custom/hire");
        meta.put("title", "发布岗位");
        meta.put("rank", 5);
        homeRouter.put("meta", meta);

        List<Map<String, Object>> children = new ArrayList<>();
        Map<String, Object> welcomeChild = new HashMap<>();
        welcomeChild.put("path", "/job/index");
        welcomeChild.put("name", "Job");
        welcomeChild.put("component", "job/index");

        Map<String, Object> welcomeMeta = new HashMap<>();
        welcomeMeta.put("title", "发布岗位");
        welcomeChild.put("meta", welcomeMeta);

        children.add(welcomeChild);
        homeRouter.put("children", children);

        return homeRouter;
    }

}
