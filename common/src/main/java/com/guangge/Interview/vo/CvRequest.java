package com.guangge.Interview.vo;

import com.guangge.Interview.record.EducationRecord;
import com.guangge.Interview.record.ProjectExperienceRecord;
import com.guangge.Interview.record.WorkExperienceRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 简历信息
 */
public class CvRequest {
    // 姓名
    private String name;
    // 性别
    private String sex;
    // 出生日期
    private String birthDate;
    // 邮箱
    private String email;
    // 电话
    private String phone;
    // JD
    private String jd;
    // 教育信息
    private List<EducationRecord> educationRecords;
    // 工作信息
    private List<WorkExperienceRecord> workExperienceRecords;
    // 项目信息
    private List<ProjectExperienceRecord> projectExperienceRecords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<EducationRecord> getEducationRecords() {
        return educationRecords;
    }

    public void setEducationRecords(List<EducationRecord> educationRecords) {
        this.educationRecords = educationRecords;
    }

    public List<WorkExperienceRecord> getWorkExperienceRecords() {
        return workExperienceRecords;
    }

    public void setWorkExperienceRecords(List<WorkExperienceRecord> workExperienceRecords) {
        this.workExperienceRecords = workExperienceRecords;
    }

    public List<ProjectExperienceRecord> getProjectExperienceRecords() {
        return projectExperienceRecords;
    }

    public void setProjectExperienceRecords(List<ProjectExperienceRecord> projectExperienceRecords) {
        this.projectExperienceRecords = projectExperienceRecords;
    }

    public String getJd() {
        return jd;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }
}
