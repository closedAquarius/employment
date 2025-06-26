package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ClassGrade implements Serializable {

    private Integer classId;

    private String className;

    private Integer specialtyId;

    private Date createTime;

    private Integer adminId;


}