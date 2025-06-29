package com.gr.geias.model;

import java.util.Date;

import lombok.Data;

/**
 * 简历基本信息实体类
 */
@Data
public class Resume {
    private Integer resumeId;
    private Long userId;
    private String resumeName;
    private Boolean isDefault;
    private Integer status; // 0-草稿，1-发布
    private Date createTime;
    private Date updateTime;
} 