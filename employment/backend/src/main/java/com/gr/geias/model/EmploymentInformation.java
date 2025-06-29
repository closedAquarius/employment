package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class EmploymentInformation implements Serializable {

    private Integer informationId;

    private Integer studentNum;

    private String name;

    private ClassGrade classGrade;

    private Area area;

    private UnitKind unitKind;

    private String salary;

    private EmploymentWay employmentWay;

    private String msg;

    private Date createTime;

    private College college;

    private Specialty specialty;

    private Integer gender;

}