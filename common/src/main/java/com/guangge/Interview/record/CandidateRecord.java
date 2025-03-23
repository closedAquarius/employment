package com.guangge.Interview.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CandidateRecord(@JsonPropertyDescription("序号") long number, @JsonPropertyDescription("候选者姓名") String name,
                              @JsonPropertyDescription("简历") String cv, @JsonPropertyDescription("邮箱") String email,
                              @JsonPropertyDescription("出生日期") Date birth, @JsonPropertyDescription("状态") String status,
                              @JsonPropertyDescription("照片Url") String pictureUrl) {}