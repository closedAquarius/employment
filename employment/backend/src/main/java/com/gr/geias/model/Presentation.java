package com.gr.geias.model;

import lombok.Data;

import java.util.Date;

@Data
public class Presentation {
    private Integer presentationId;   // 宣讲会ID
    private Integer companyId;        // 公司用户ID（tb_person_info.person_id）
    private String title;             // 宣讲会标题
    private String location;          // 举办地点
    private Integer capacity;         // 地点可容纳人数
    private Date startTime;           // 开始时间
    private Date endTime;             // 结束时间
    private String description;       // 宣讲会内容描述
    private Integer status;           // 状态（0=待审批，1=通过，2=拒绝）
    private String remark;            // 新增备注字段
    private Date createTime;          // 申请时间
    private Date updateTime;          // 更新时间
    private int signupCount;          // 报名人数

}
