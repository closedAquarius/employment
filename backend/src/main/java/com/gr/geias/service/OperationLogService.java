package com.gr.geias.service;

import com.gr.geias.model.OperationLog;
import java.util.List;

public interface OperationLogService {
    void saveLog(OperationLog log);
    List<OperationLog> searchLogs(Integer enableStatus, String startTime, String endTime, String operationType, int pageNum, int pageSize);
    long countLogs(Integer enableStatus, String startTime, String endTime, String operationType);
}