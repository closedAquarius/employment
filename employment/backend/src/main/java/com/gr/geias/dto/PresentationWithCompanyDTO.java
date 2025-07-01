package com.gr.geias.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PresentationWithCompanyDTO {
    private Integer presentationId;
    private String title;
    private String location;
    private Integer capacity;
    private Date startTime;
    private Date endTime;
    private String description;
    private Integer status;
    private String remark;
    private Integer signupCount;  // ✅ 当前报名人数
    private Date createTime;
    private Date updateTime;

    // 公司信息字段
    private Integer companyId;
    private String companyName;
    private String companyType; // 来自 unit_kind 表

}
