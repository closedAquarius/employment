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
    static {
        TARGET_DESCRIPTIONS.put("/personinfo/login", "用户登录");
        TARGET_DESCRIPTIONS.put("/personinfo/register", "用户注册");
        TARGET_DESCRIPTIONS.put("/personinfo/getuser", "获取当前用户信息");
        TARGET_DESCRIPTIONS.put("/personinfo/updateuser", "更新用户信息");
        TARGET_DESCRIPTIONS.put("/personinfo/faceLogin", "人脸识别登录");
        TARGET_DESCRIPTIONS.put("/personinfo/addFace", "添加人脸信息");
        TARGET_DESCRIPTIONS.put("/companyinfo/company-info", "获取企业信息");
        TARGET_DESCRIPTIONS.put("/companyinfo/update-company", "修改企业信息");
        TARGET_DESCRIPTIONS.put("/companyinfo/confirm-company", "管理员确认企业信息");
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
        return TARGET_DESCRIPTIONS.getOrDefault(uri, "未知操作：" + uri);
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