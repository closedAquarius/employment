package com.gr.geias.model;

import lombok.Data;

import java.util.Date;

@Data
public class JobFair {
    private Integer jobFairId;         // 招聘会ID
    private String title;              // 招聘会主题
    private String description;        // 简介
    private String location;           // 举办地点
    private Date startTime;   // 开始时间
    private Date endTime;     // 结束时间
    private Date createTime;  // 创建时间
    private Integer organizerId;       // 发布者ID（person_id）
    private Integer boothTotal;        // 新增字段：展位总数
}
