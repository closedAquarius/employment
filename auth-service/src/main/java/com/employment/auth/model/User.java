package com.employment.auth.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 统一认证用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private Integer status; // 0-禁用，1-启用
    private String avatar;
    private Integer userType; // 0-学生，1-教师，2-管理员，3-企业HR
    private Date createTime;
    private Date updateTime;
    private String sourceSystem; // 用户来源系统：employment或ai-interview
    private Long sourceId; // 源系统中的用户ID
} 