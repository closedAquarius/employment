package com.gr.geias.aspect;
import com.gr.geias.model.OperationLog;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.service.OperationLogService;
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
     * 获取会话中的 PersonInfo
     */
    private PersonInfo getPersonInfo(HttpServletRequest request) {
        return (PersonInfo) request.getSession().getAttribute("person");
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
        return request.getRequestURI();
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