package com.gr.geias.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyInfo {
    private Integer companyId;         // 企业ID
    private String companyName;        // 企业名称
    private String companyIntro;       // 企业简介
    private String address;            // 企业地址
    private Integer areaId;            // 所属区域ID
    private LocalDateTime createTime;  // 创建时间
    private Integer UnitId;            // 所属单位类型
    private Boolean confirmed;        // 企业是否被确认


    // 可选：关联的区域名（关联 tb_area）
    private String areaName;
}
