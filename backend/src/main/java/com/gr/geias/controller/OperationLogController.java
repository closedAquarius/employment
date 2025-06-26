package com.gr.geias.controller;

import com.gr.geias.model.OperationLog;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志控制器，提供日志查询功能
 */
@RestController
@RequestMapping("/logs")
public class OperationLogController {
    private static final Logger logger = LoggerFactory.getLogger(OperationLogController.class);

    @Autowired
    private OperationLogService logService;

    /**
     * 查询日志列表
     * @param enableStatus 角色状态（如 0=学生，1=教师，2=管理员）
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param operationType 操作类型（如 GET /employmentinformation/getemploymentinfo）
     * @param pageNum 页码，默认 1
     * @param pageSize 每页记录数，默认 10
     * @param request HTTP 请求，用于获取用户信息
     * @return 日志列表和总数
     */
    @GetMapping("/searchlogs")
    public Map<String, Object> searchLogs(
            @RequestParam(value = "enableStatus", required = false) Integer enableStatus,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "operationType", required = false) String operationType,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取当前用户信息
            PersonInfo person = (PersonInfo) request.getSession().getAttribute("person");
            if (person == null) {
                result.put("success", false);
                result.put("errMsg", "用户未登录");
                return result;
            }

            // 权限控制：学生只能查看自己的日志
            Integer queryEnableStatus = enableStatus;
            if (person.getEnableStatus() == 0) {
                queryEnableStatus = 0;
            }

            // 调用服务层查询日志
            List<OperationLog> logs = logService.searchLogs(queryEnableStatus, startTime, endTime, operationType, pageNum, pageSize);
            long total = logService.countLogs(queryEnableStatus, startTime, endTime, operationType);

            result.put("success", true);
            result.put("list", logs);
            result.put("total", total);
            result.put("errMsg", null);
            logger.info("日志查询成功: user={}, enableStatus={}, pageNum={}, pageSize={}",
                    person.getUsername(), queryEnableStatus, pageNum, pageSize);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errMsg", "查询失败: " + e.getMessage());
            result.put("list", null);
            result.put("total", 0);
            logger.error("日志查询失败: {}", e.getMessage());
        }
        return result;
    }
}