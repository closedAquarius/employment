package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专业信息实体类
 */
@Data
public class Specialty implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专业ID
     */
    private Integer specialtyId;

    /**
     * 专业名称
     */
    private String specialtyName;

    /**
     * 所属学院ID
     */
    private Integer collegeId;
    
    /**
     * 创建时间
     */
    private Date createTime;
}