package com.guangge.Interview.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterViewRecord(@JsonPropertyDescription("序号") String number, @JsonPropertyDescription("姓名") String name,
                              @JsonPropertyDescription("分数") int score, @JsonPropertyDescription("状态") String interViewStatus,
                              @JsonPropertyDescription("笔试评语") String evaluate, @JsonPropertyDescription("邮箱") String email,
                              @JsonPropertyDescription("音频路径") String mp3path, @JsonPropertyDescription("面试评语") String interviewEvaluate) {}
