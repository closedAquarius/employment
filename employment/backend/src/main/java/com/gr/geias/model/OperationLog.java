package com.gr.geias.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private Integer personId;
    private Integer enableStatus;
    private String username;
    private String operationType; // 操作类型
    private String target; // 操作目标
    private String details; // 操作详情
    private String ipAddress; // IP地址
    private LocalDateTime operationTime; // 操作时间
    private Boolean success; // 操作结果
    private String errorMsg;
}