package com.guangge.Interview.record;

import java.time.LocalDate;
import java.util.Date;

public class ProjectExperienceRecord {
    // 项目名称
    private String name;
    // 开始日期
    private String begin;
    // 结束日期
    private String end;
    // 工作内容
    private String content;
    // 使用技术
    private String skills;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
