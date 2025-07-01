package com.gr.geias.aspect;
import com.gr.geias.model.OperationLog;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.service.OperationLogService;
import com.gr.geias.service.PersonInfoService;
import com.gr.geias.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 日志切面，拦截控制器方法，记录操作日志
 */
@Aspect
@Component
public class OperationLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private PersonInfoService personInfoService;

    // 定义切点，拦截 com.gr.geias.controller 包下所有类的所有方法
    private static final String POINT_CUT = "execution(* com.gr.geias.controller..*(..))";

    /**
     * 记录成功操作的日志
     * @param result 方法返回值
     */
    @AfterReturning(pointcut = POINT_CUT, returning = "result")
    public void logAfterReturning(Object result) {
        HttpServletRequest request = getRequest();
        PersonInfo person = getPersonInfo(request);
        if (person == null) {
            logger.warn("未登录用户，跳过日志记录");
            return;
        }

        OperationLog log = buildLog(request, person);
        log.setSuccess(true);
        log.setDetails(extractDetails(request, result));
        operationLogService.saveLog(log);
        logger.info("记录成功操作日志: {}", log);
    }

    /**
     * 记录失败操作的日志
     * @param ex 异常
     */
    @AfterThrowing(pointcut = POINT_CUT, throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        HttpServletRequest request = getRequest();
        PersonInfo person = getPersonInfo(request);
        if (person == null) {
            logger.warn("未登录用户，跳过日志记录");
            return;
        }

        //不统计welcome信息
        String uri = request.getRequestURI();
        if (uri.startsWith("/welcome")) {
            return;
        }

        OperationLog log = buildLog(request, person);
        log.setSuccess(false);
        log.setErrorMsg(ex.getMessage());
        log.setDetails("Exception: " + ex.getMessage());
        operationLogService.saveLog(log);
        logger.info("记录失败操作日志: {}", log);
    }

    /**
     * 获取 HttpServletRequest
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest() : null;
    }

    /**
     * 从请求头 Authorization 里解析 JWT token，获得用户信息
     */
    private PersonInfo getPersonInfo(HttpServletRequest request) {
        if (request == null) return null;
        try {
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return null;
            }
            // 如果 token 是 Bearer 开头，去除 Bearer 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = JwtUtil.parseAccessToken(token);
            Integer userId = (Integer) claims.get("userId");
            if (userId == null) {
                return null;
            }

            // 调用 service 查数据库获取用户详情
            return personInfoService.getPersonById(userId);
        } catch (Exception e) {
            logger.warn("解析token失败，无法获取用户信息", e);
            return null;
        }
    }

    //target字段的描述 URI -> 中文操作描述映射
    private static final Map<String, String> TARGET_DESCRIPTIONS = new HashMap<>();
    private static final Map<String, Pattern> URI_PATTERNS = new HashMap<>();
    static {
        TARGET_DESCRIPTIONS.put("/personinfo/login", "用户登录");
        TARGET_DESCRIPTIONS.put("/personinfo/get-async-routes", "获取动态路由");
        TARGET_DESCRIPTIONS.put("/personinfo/register", "用户注册");
        TARGET_DESCRIPTIONS.put("/personinfo/getuser", "获取当前用户信息");
        TARGET_DESCRIPTIONS.put("/personinfo/updateuser", "更新用户信息");
        TARGET_DESCRIPTIONS.put("/personinfo/faceLogin", "人脸识别登录");
        TARGET_DESCRIPTIONS.put("/personinfo/addFace", "添加人脸信息");
        TARGET_DESCRIPTIONS.put("/companyinfo/company-info", "获取企业信息");
        TARGET_DESCRIPTIONS.put("/companyinfo/update-company", "修改企业信息");
        TARGET_DESCRIPTIONS.put("/companyinfo/confirm-company", "管理员确认企业信息");
        TARGET_DESCRIPTIONS.put("/employmentinformation/getemploymentinfo", "获取毕业生就业信息列表");
        TARGET_DESCRIPTIONS.put("/employmentinformation/getcountbyarea", "获取地区数量列表");
        TARGET_DESCRIPTIONS.put("/employmentinformation/getcountbyemploymentway","获取就业途径数量列表 ");
        TARGET_DESCRIPTIONS.put("/employmentinformation/getcountbyunitkind", "获取职业分类数量 ");
        TARGET_DESCRIPTIONS.put("/fairs/JobFairWithCompanies", "查看招聘会和公司信息");
        TARGET_DESCRIPTIONS.put("/fairs/jobfair", "发布招聘会");
        TARGET_DESCRIPTIONS.put("/fairs/jobfairsWithboothstatus", "查看所有招聘会 + 当前占用展位编号");
        TARGET_DESCRIPTIONS.put("/fairs/jobfairsUnapplied", "查看未申请的招聘会 + 剩余展位编号");
        TARGET_DESCRIPTIONS.put("/fairs/company/jobfairs/applied", "查看已申请的招聘会");
        TARGET_DESCRIPTIONS.put("/fairs/companyApply", "申请加入某场招聘会");
        TARGET_DESCRIPTIONS.put("/fairs/adminReview", "审批公司加入申请");
        TARGET_DESCRIPTIONS.put("/info/getcollege", "获取学院列表");
        TARGET_DESCRIPTIONS.put("/info/getspecialty", "获取专业列表");
        TARGET_DESCRIPTIONS.put("/info/getclassgrade", "获取班级列表");
        TARGET_DESCRIPTIONS.put("/info/getinit", "获取初始化数据");
        TARGET_DESCRIPTIONS.put("/info/addemploymentinfo", "添加或更新就业信息");
        TARGET_DESCRIPTIONS.put("/init/getinit", "获取普通分类信息");
        TARGET_DESCRIPTIONS.put("/init/getleve", "获取高级分类信息");
        TARGET_DESCRIPTIONS.put("/api/jobs/import", "导入职位信息");
        TARGET_DESCRIPTIONS.put("/message/send", "发送私聊消息");
        TARGET_DESCRIPTIONS.put("/message/contacts/{userId}", "获取聊天联系人列表");
        TARGET_DESCRIPTIONS.put("/message/chatHistory", "获取聊天记录");
        TARGET_DESCRIPTIONS.put("/message/deleteMessage", "删除私聊消息");
        TARGET_DESCRIPTIONS.put("/message/uploadFile", "上传文件消息");
        TARGET_DESCRIPTIONS.put("/message/uploadImage", "上传图片消息");
        TARGET_DESCRIPTIONS.put("/news/uploadImages", "上传新闻图片");
        TARGET_DESCRIPTIONS.put("/news/publish", "发布新闻");
        TARGET_DESCRIPTIONS.put("/news/update", "更新新闻信息");
        TARGET_DESCRIPTIONS.put("/news/list", "分页获取新闻列表");
        TARGET_DESCRIPTIONS.put("/news/hot", "获取热门新闻");
        TARGET_DESCRIPTIONS.put("/news/latest", "获取最新新闻");
        TARGET_DESCRIPTIONS.put("/news/search", "搜索新闻");
        TARGET_DESCRIPTIONS.put("/news/{newsId}/comment", "发表新闻评论");
        TARGET_DESCRIPTIONS.put("/news/{newsId}/comments", "获取新闻评论列表");
        TARGET_DESCRIPTIONS.put("/news/{newsId}/commentsWithPerson", "分页获取新闻评论及用户信息");
        TARGET_DESCRIPTIONS.put("/news/comment/{commentId}", "删除新闻评论");
        TARGET_DESCRIPTIONS.put("/news/admin/comments", "分页获取所有新闻评论");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getcollegelist", "获取学院详细列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getcollegeadmin", "获取空闲学院管理员");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/addcollege", "添加学院");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/updatecollege", "更新学院信息");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/delcollege", "删除学院");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getcollegeinit", "获取学院简化列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getspecialty", "获取专业详细列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/addspecialty", "添加专业");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/updatespecialty", "更新专业信息");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/delspecialty", "删除专业");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getspecialtyinit", "获取专业简化列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getclassgrade", "获取班级详细列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getpersoninit", "获取学院内老师列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/addclassgrade", "添加班级");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/updateclassgrade", "更新班级信息");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/delclassgrade", "删除班级");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getperson_0", "获取辅导员列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/addperson_o", "添加辅导员");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getpersonById", "获取用户信息");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/updateperson_0", "更新辅导员信息");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/delperson_0", "删除辅导员");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getperson_1", "获取学院管理员列表");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/addperson_1", "添加学院管理员");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/updateperson_1", "更新学院管理员信息");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/delperson_1", "删除学院管理员");
        TARGET_DESCRIPTIONS.put("/organizationcontroller/getorganizationinfo", "获取组织架构图数据");
        TARGET_DESCRIPTIONS.put("/presentation/apply", "申请举办宣讲会");
        TARGET_DESCRIPTIONS.put("/presentation/company/{companyId}", "获取公司申请的宣讲会列表");
        TARGET_DESCRIPTIONS.put("/presentation/admin/presentations", "获取所有宣讲会申请记录");
        TARGET_DESCRIPTIONS.put("/presentation/{presentationId}/approve", "审批通过宣讲会申请");
        TARGET_DESCRIPTIONS.put("/presentation/{presentationId}/reject", "拒绝宣讲会申请");
        TARGET_DESCRIPTIONS.put("/presentation/{presentationId}/signup", "学生报名宣讲会");
        TARGET_DESCRIPTIONS.put("/presentation/student/signed", "获取学生已报名宣讲会列表");
        TARGET_DESCRIPTIONS.put("/presentation/student/unsigned", "获取学生未报名宣讲会列表");
        TARGET_DESCRIPTIONS.put("/presentation/student/cancel", "学生撤销宣讲会报名");
        TARGET_DESCRIPTIONS.put("/presentation/specialty/{presentationId}", "获取宣讲会专业分布");
        TARGET_DESCRIPTIONS.put("/presentation/class/{presentationId}", "获取宣讲会班级分布");

        for (String uriTemplate : TARGET_DESCRIPTIONS.keySet()) {

            String regex = uriTemplate.replaceAll("\\{[^}]+\\}", "\\\\d+");
            URI_PATTERNS.put(uriTemplate, Pattern.compile("^" + regex + "$"));
        }
    }


    /**
     * 构建 Log 对象
     */
    private OperationLog buildLog(HttpServletRequest request, PersonInfo person) {
        OperationLog log = new OperationLog();
        log.setPersonId(person.getPersonId());
        log.setEnableStatus(person.getEnableStatus());
        log.setUsername(person.getUsername());
        log.setOperationType(request.getMethod() + " " + request.getRequestURI());
        log.setTarget(extractTarget(request));
        log.setIpAddress(request.getRemoteAddr());
        log.setOperationTime(LocalDateTime.now());
        return log;
    }

    /**
     * 提取操作目标
     */
    private String extractTarget(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (Map.Entry<String, Pattern> entry : URI_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(uri).matches()) {
                return TARGET_DESCRIPTIONS.get(entry.getKey());
            }
        }
        return "未知操作：" + uri;
    }


    /**
     * 提取操作详情
     */
    private String extractDetails(HttpServletRequest request, Object result) {
        Map<String, String[]> paramMap = new HashMap<>(request.getParameterMap());
        paramMap.remove("password"); // 避免记录密码
        return "Params: " + paramMap + ", Result: " + (result != null ? result.toString() : "null");
    }
}