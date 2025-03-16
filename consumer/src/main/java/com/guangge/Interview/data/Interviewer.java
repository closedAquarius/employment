package com.guangge.Interview.data;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "Interviewer")
public class Interviewer {
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

    @Column(name = "status", nullable = true)
    private int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
