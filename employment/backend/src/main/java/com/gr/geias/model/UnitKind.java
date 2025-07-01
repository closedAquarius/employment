package com.gr.geias.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UnitKind implements Serializable {

    private Integer unitId;

    private String unitName;

    private Date createTime;


}