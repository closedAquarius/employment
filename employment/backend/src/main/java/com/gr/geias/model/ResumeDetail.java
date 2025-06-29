package com.gr.geias.model;

import lombok.Data;
/**
 * 简历详细内容实体类
 */
@Data
public class ResumeDetail {
    private Integer detailId;
    private Integer resumeId;
    private String basicInfo; // JSON字符串
    private String education; // JSON字符串
    private String workExperience; // JSON字符串
    private String projectExperience; // JSON字符串
    private String skills; // JSON字符串
    private String certificates; // JSON字符串
    private String selfEvaluation;
    private String attachmentUrl;
} 