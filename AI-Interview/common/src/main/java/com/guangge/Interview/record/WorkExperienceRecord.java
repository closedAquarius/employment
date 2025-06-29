package com.guangge.Interview.record;

import java.time.LocalDate;
import java.util.Date;

public class WorkExperienceRecord {
    // 公司
    private String company;
    // 入职日期
    private String begin;
    // 离职日期
    private String end;
    // 工作内容
    private String workContent;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }
}
