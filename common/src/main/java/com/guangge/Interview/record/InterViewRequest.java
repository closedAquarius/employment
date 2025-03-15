package com.guangge.Interview.record;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record InterViewRequest(@JsonPropertyDescription("序号,比如1,2,3,4") String number, @JsonPropertyDescription("姓名,比如张三，李四") String name,
                               @JsonPropertyDescription("分数") int score, @JsonPropertyDescription("评语") String evaluate){}
