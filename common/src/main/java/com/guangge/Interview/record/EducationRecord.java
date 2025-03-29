package com.guangge.Interview.record;

import java.time.LocalDate;
import java.util.Date;

public class EducationRecord {
    //  大学名称
    private String university;
    // 开始日期
    private String begin;
    // 结束日期
    private String end;
    // 专业
    private String major;
    // 学位
    private String degree;

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}
