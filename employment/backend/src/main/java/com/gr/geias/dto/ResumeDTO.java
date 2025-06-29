package com.gr.geias.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 简历数据传输对象
 */
@Data
public class ResumeDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 简历ID
     */
    private Integer resumeId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 简历名称
     */
    private String resumeName;
    
    /**
     * 是否默认简历
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
    
    /**
     * 基本信息
     */
    private Map<String, Object> basicInfo;
    
    /**
     * 教育经历
     */
    private List<Map<String, Object>> education;
    
    /**
     * 工作经历
     */
    private List<Map<String, Object>> workExperience;
    
    /**
     * 项目经历
     */
    private List<Map<String, Object>> projectExperience;
    
    /**
     * 技能特长
     */
    private List<String> skills;
    
    /**
     * 证书
     */
    private List<Map<String, Object>> certificates;
    
    /**
     * 自我评价
     */
    private String selfEvaluation;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
} 