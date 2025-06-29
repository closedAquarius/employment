package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 人员信息实体类
 */
@Data
public class PersonInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer personId;

    private Integer enableStatus;

    private String personName;

    private Date createTime;

    private String password;

    private String username;

    private Integer collegeId;

    private String faceToken;
} 