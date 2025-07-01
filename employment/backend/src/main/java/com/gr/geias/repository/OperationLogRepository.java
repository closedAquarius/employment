package com.gr.geias.repository;
import com.gr.geias.model.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface OperationLogRepository{

    /**
     * 插入日志记录
     */
    @Insert("INSERT INTO operation_log (person_id, enable_status, username, operation_type, target, details, ip_address, operation_time, success, error_msg)\n" +
            "VALUES (#{personId}, #{enableStatus}, #{username}, #{operationType}, #{target}, #{details}, #{ipAddress}, #{operationTime}, #{success}, #{errorMsg})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OperationLog operationLog);

    /**
     * 查询日志列表，支持多条件过滤和分页
     * @param enableStatus 角色状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param operationType 操作类型
     * @param offset 分页偏移量
     * @param pageSize 每页记录数
     * @return 日志列表
     */
    List<OperationLog> searchLogs(
            @Param("enableStatus") Integer enableStatus,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("operationType") String operationType,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计符合条件的日志数量
     * @param enableStatus 角色状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param operationType 操作类型
     * @return 日志总数
     */
    long countLogs(
            @Param("enableStatus") Integer enableStatus,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("operationType") String operationType);
}