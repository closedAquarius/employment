package com.guangge.Interview.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.guangge.Interview.services.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * 笔试需要的FunctionCall
 */
@Configuration
public class WrittenTestTools {
    @Autowired
    private ResumeService resumeService;

    public record InterViewRequest(@JsonPropertyDescription("序号,比如1,2,3,4") String number, @JsonPropertyDescription("姓名,比如张三，李四") String name,
                                   @JsonPropertyDescription("分数") int score, @JsonPropertyDescription("评语") String evaluate){}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InterViewRecord(@JsonPropertyDescription("序号") String number, @JsonPropertyDescription("姓名") String name,
                                  @JsonPropertyDescription("分数") int score, @JsonPropertyDescription("状态") String interViewStatus,
                                  @JsonPropertyDescription("笔试评语") String evaluate,@JsonPropertyDescription("邮箱") String email,
                                  @JsonPropertyDescription("音频路径") String mp3path,@JsonPropertyDescription("面试评语") String interviewEvaluate) {}

    @Bean
    @Description("更改笔试结果")
    public Function<WrittenTestTools.InterViewRequest, String> changeTestResult() {
        return request -> {
            resumeService.changeTestReuslt(request.name(), request.score(), request.evaluate());
            return "";
        };
    }

    @Bean
    @Description("获取笔试结果")
    public Function<WrittenTestTools.InterViewRequest, WrittenTestTools.InterViewRecord> getInterviewDetails() {
        return request -> {
            try {
                return resumeService.getInterViewDetails(request.name());
            }
            catch (Exception e) {
                return null;
            }
        };
    }
}
