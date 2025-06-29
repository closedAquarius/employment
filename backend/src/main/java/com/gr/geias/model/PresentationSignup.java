package com.gr.geias.model;

import lombok.Data;

import java.util.Date;

@Data
public class PresentationSignup {
    private Integer signupId;           // 报名记录ID
    private Integer presentationId;     // 宣讲会ID
    private Integer studentId;          // 报名学生ID（对应 tb_person_info.person_id，要求 enable_status=0）
    private Date signupTime;            // 报名时间
}
