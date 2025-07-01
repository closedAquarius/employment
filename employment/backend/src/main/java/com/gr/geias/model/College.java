package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class College implements Serializable {

    private Integer collegeId;

    private String collegeName;

    private Date createTime;

    private Integer adminId;


}