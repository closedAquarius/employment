package com.gr.geias.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 岗位信息实体类
 */
@Data
public class JobPosition implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 岗位ID
     */
    private Integer jobId;
    
    /**
     * 企业ID
     */
    private Integer companyId;
    
    /**
     * 岗位标题
     */
    private String title;
    
    /**
     * 岗位描述
     */
    private String description;
    
    /**
     * 任职要求
     */
    private String requirements;
    
    /**
     * 薪资范围
     */
    private String salaryRange;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 状态（0-下架，1-发布）
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