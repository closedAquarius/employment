package com.gr.geias.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 简历投递记录实体类
 */
@Data
public class ResumeDelivery implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 投递ID
     */
    private Integer deliveryId;
    
    /**
     * 简历ID
     */
    private Integer resumeId;
    
    /**
     * 岗位ID
     */
    private Integer jobId;
    
    /**
     * 企业ID
     */
    private Integer companyId;
    
    /**
     * 状态（0-待查看，1-已查看，2-邀请面试，3-不合适）
     */
    private Integer status;
    
    /**
     * 投递时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    
    /**
     * 企业反馈
     */
    private String feedback;
} 