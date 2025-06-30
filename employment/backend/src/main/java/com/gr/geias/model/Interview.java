package com.gr.geias.model;

import java.util.Date;

/**
 * 面试实体类
 */
public class Interview {
    
    private Long id;              // 面试ID
    private Long resumeId;        // 简历ID
    private Long positionId;      // 职位ID
    private Integer status;       // 面试状态：1-待面试，2-面试中，3-面试通过，4-面试未通过，5-已取消
    private String remarks;       // 面试备注/反馈
    private Date interviewTime;   // 面试时间
    private String location;      // 面试地点
    private String interviewer;   // 面试官
    private Date createTime;      // 创建时间
    private Date updateTime;      // 更新时间
    
    // 扩展字段，不存储在数据库
    private String resumeName;    // 简历姓名
    private String positionName;  // 职位名称
    private String companyName;   // 公司名称
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getResumeId() {
        return resumeId;
    }
    
    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }
    
    public Long getPositionId() {
        return positionId;
    }
    
    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Date getInterviewTime() {
        return interviewTime;
    }
    
    public void setInterviewTime(Date interviewTime) {
        this.interviewTime = interviewTime;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getInterviewer() {
        return interviewer;
    }
    
    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
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
    
    public String getResumeName() {
        return resumeName;
    }
    
    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }
    
    public String getPositionName() {
        return positionName;
    }
    
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
} 