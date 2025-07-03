package com.gr.geias.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 简历基本信息实体类
 */
@Data
public class Resume implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 简历ID
     */
    private Integer resumeId;
    
    /**
     * 关联统一认证用户ID
     */
    private Long userId;
    
    /**
     * 简历名称
     */
    private String resumeName;
    
    /**
     * 是否默认简历（0-否，1-是）
     */
    private Boolean isDefault;
    
    /**
     * 状态（0-草稿，1-发布）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
} 