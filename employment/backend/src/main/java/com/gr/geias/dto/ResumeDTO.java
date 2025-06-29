package com.gr.geias.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 简历数据传输对象
 */
public class ResumeDTO {
    private Integer resumeId;
    private Long userId;
    private String resumeName;
    private Boolean isDefault;
    private Integer status;
    
    // 基本信息
    private Map<String, Object> basicInfo;
    
    // 教育经历
    private List<Map<String, Object>> education;
    
    // 工作经历
    private List<Map<String, Object>> workExperience;
    
    // 项目经历
    private List<Map<String, Object>> projectExperience;
    
    // 技能特长
    private List<Map<String, Object>> skills;
    
    // 证书
    private List<Map<String, Object>> certificates;
    
    // 自我评价
    private String selfEvaluation;
    
    // 附件URL
    private String attachmentUrl;
    
    // 创建时间
    private Date createTime;
    
    // 更新时间
    private Date updateTime;

    public Integer getResumeId() {
        return resumeId;
    }

    public void setResumeId(Integer resumeId) {
        this.resumeId = resumeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getResumeName() {
        return resumeName;
    }

    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, Object> getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(Map<String, Object> basicInfo) {
        this.basicInfo = basicInfo;
    }

    public List<Map<String, Object>> getEducation() {
        return education;
    }

    public void setEducation(List<Map<String, Object>> education) {
        this.education = education;
    }

    public List<Map<String, Object>> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(List<Map<String, Object>> workExperience) {
        this.workExperience = workExperience;
    }

    public List<Map<String, Object>> getProjectExperience() {
        return projectExperience;
    }

    public void setProjectExperience(List<Map<String, Object>> projectExperience) {
        this.projectExperience = projectExperience;
    }

    public List<Map<String, Object>> getSkills() {
        return skills;
    }

    public void setSkills(List<Map<String, Object>> skills) {
        this.skills = skills;
    }

    public List<Map<String, Object>> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Map<String, Object>> certificates) {
        this.certificates = certificates;
    }

    public String getSelfEvaluation() {
        return selfEvaluation;
    }

    public void setSelfEvaluation(String selfEvaluation) {
        this.selfEvaluation = selfEvaluation;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
} 