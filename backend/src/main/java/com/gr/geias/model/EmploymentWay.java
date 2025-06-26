package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class EmploymentWay implements Serializable {

    private Integer employmentWayId;

    private String vayName;

    private Date createTime;


}