package com.guangge.Interview.data;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "candidates")
public class Candidates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 姓名
    @Column(name = "candidate_name", nullable = false, unique = true)
    private String name;

    // 姓名
    @Column(name = "invitation_code", nullable = false, unique = true)
    private String code;

    // 简历
    @Column(name = "cv", nullable = true)
    private String cv;

    // 邮箱
    @Column(name = "email", nullable = true)
    private String email;

    // 出生日期
    @Column(name = "birth", nullable = true)
    private Date birth;

    // 状态 1.待定 2.通知面试 3.面试完毕
    @Column(name = "status", nullable = true)
    @Convert(converter = CandidateStatusConverter.class)
    private CandidateStatus status;

    @Column(name = "picture_url", nullable = true)
    private String pictureUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
