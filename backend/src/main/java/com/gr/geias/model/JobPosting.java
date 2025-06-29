package com.gr.geias.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobPosting {
    @JsonProperty("职位ID")
    private Integer positionId;
    @JsonProperty("职位名称")
    private String positionName;
    @JsonProperty("城市")
    private String city;
    @JsonProperty("薪资")
    private String salary;
    @JsonProperty("公司名称")
    private String companyName;
    @JsonProperty("公司标签")
    private List<String> companyTags;        // 存 JSON 字符串，或 List<String> 并使用 TypeHandler
    @JsonProperty("学历要求")
    private String educationRequirement;
    @JsonProperty("是否需要工作经验")
    private String experienceRequired;
    @JsonProperty("专业ID")
    private String jobId;
    @JsonProperty("招聘人数")
    private String recruitNumber;
    @JsonProperty("优选专业")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> preferredMajors;
    @JsonProperty("更新时间")
    private Date date;
    @JsonProperty("职位描述")
    private String description;
    @JsonProperty("工作地点")
    private String place;
}
