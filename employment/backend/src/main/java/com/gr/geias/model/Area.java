package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Area implements Serializable {

    private Integer areaId;

    private String areaName;

    private Integer parentId;

    private Date createTime;


} 