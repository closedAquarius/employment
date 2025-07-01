package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 简历详细内容实体类
 */
@Data
public class ResumeDetail implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 详情ID
     */
    private Integer detailId;
    
    /**
     * 关联简历ID
     */
    private Integer resumeId;
    
    /**
     * 基本信息(姓名、性别、出生日期等)，JSON格式
     */
    private String basicInfo;
    
    /**
     * 教育经历，JSON格式
     */
    private String education;
    
    /**
     * 工作经历，JSON格式
     */
    private String workExperience;
    
    /**
     * 项目经历，JSON格式
     */
    private String projectExperience;
    
    /**
     * 技能特长，JSON格式
     */
    private String skills;
    
    /**
     * 证书，JSON格式
     */
    private String certificates;
    
    /**
     * 自我评价
     */
    private String selfEvaluation;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
} 