package com.gr.geias.model;

import lombok.Data;

@Data
public class JobFairCompany {
    private Integer jobFairId;      // 招聘会ID
    private Integer companyId;      // 企业ID
    private int boothLocation;   // 展位位置（可选）
    private int status;             // 状态（0：未审核，1：已审核，2：已拒绝）

    // 可选：联合查询扩展字段
    private String jobFairTitle;    // 招聘会标题
    private String companyName;     // 企业名称
}
