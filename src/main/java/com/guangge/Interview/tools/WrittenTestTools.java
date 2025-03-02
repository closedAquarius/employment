package com.guangge.Interview.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.guangge.Interview.assistant.record.InterViewRecord;
import com.guangge.Interview.assistant.record.InterViewRequest;
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

    @Bean
    @Description("更改笔试结果")
    public Function<InterViewRequest, String> changeTestResult() {
        return request -> {
            resumeService.changeTestReuslt(request.name(), request.score(), request.evaluate());
            return "";
        };
    }

    @Bean
    @Description("获取笔试结果")
    public Function<InterViewRequest, InterViewRecord> getInterviewDetails() {
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
