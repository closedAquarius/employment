package com.gr.geias.dto;


import lombok.Data;

import java.util.Date;

@Data
public class StudentWithCollege {
    private Integer personId;

    private String personName;

    private Date createTime;

    private String password;

    private String username;

    private Integer collegeId;

    private String collegeName;
}
