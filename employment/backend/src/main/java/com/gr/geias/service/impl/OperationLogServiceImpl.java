package com.gr.geias.service.impl;

import com.gr.geias.model.OperationLog;
import com.gr.geias.repository.OperationLogRepository;
import com.gr.geias.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {
    private static final Logger logger = LoggerFactory.getLogger(OperationLogServiceImpl.class);

    @Autowired
    private OperationLogRepository operationLogRepository;

    public void saveLog(OperationLog log) {
        operationLogRepository.insert(log);
    }

    @Override
    public List<OperationLog> searchLogs(Integer enableStatus, String startTime, String endTime, String operationType, int pageNum, int pageSize) {
        try {
            // 验证时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = startTime != null && !startTime.isEmpty() ? LocalDateTime.parse(startTime, formatter) : null;
            LocalDateTime end = endTime != null && !endTime.isEmpty() ? LocalDateTime.parse(endTime, formatter) : null;

            // 验证分页参数
            if (pageNum < 1 || pageSize < 1) {
                throw new IllegalArgumentException("分页参数无效");
            }

            List<OperationLog> logs = operationLogRepository.searchLogs(enableStatus, start, end, operationType, (pageNum - 1) * pageSize, pageSize);
            logger.info("日志查询成功: enableStatus={}, startTime={}, endTime={}, operationType={}, pageNum={}, pageSize={}",
                    enableStatus, startTime, endTime, operationType, pageNum, pageSize);
            return logs;
        } catch (Exception e) {
            logger.error("日志查询失败: {}", e.getMessage());
            throw new RuntimeException("日志查询失败", e);
        }
    }

    @Override
    public long countLogs(Integer enableStatus, String startTime, String endTime, String operationType) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = startTime != null && !startTime.isEmpty() ? LocalDateTime.parse(startTime, formatter) : null;
            LocalDateTime end = endTime != null && !endTime.isEmpty() ? LocalDateTime.parse(endTime, formatter) : null;

            long total = operationLogRepository.countLogs(enableStatus, start, end, operationType);
            logger.info("日志计数成功: total={}", total);
            return total;
        } catch (Exception e) {
            logger.error("日志计数失败: {}", e.getMessage());
            throw new RuntimeException("日志计数失败", e);
        }
    }
}