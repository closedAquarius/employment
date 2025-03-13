package com.guangge.Interview.tools;

import com.guangge.Interview.record.InterViewRequest;
import com.guangge.Interview.services.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * 面试需要的FunctionCall
 */
@Configuration
public class Face2FaceTools {
    @Autowired
    private ResumeService resumeService;

    @Bean
    @Description("根据姓名获取简历")
    public Function<InterViewRequest, String> getResumeByName() {
        return request -> {
            try {
                String resume = resumeService.findInterView(request.name()).getRawText();
                if (resume.isEmpty()) {
                    return "查无此人可以让面试者提供序号调用 getResumeById 函数获取简历";
                }
                return resume;
            } catch (IllegalArgumentException ex) {
                return "没有发现这个人，请确认真的被邀请面试了吗。";
            }
        };
    }

    @Bean
    @Description("根据序号获取简历")
    public Function<InterViewRequest, String> getResumeById() {
        return request -> {
            try {
                String resume = resumeService.findInterView(Long.valueOf(request.number())).getRawText();
                if (resume.isEmpty()) {
                    return "没有发现这个人的简历，请确认他真实姓名。";
                }
                return resume;
            } catch (IllegalArgumentException ex) {
                return "没有发现这个人，请确认真的被邀请面试了吗。";
            }
        };
    }

    @Bean
    @Description("更改面试结果")
    public Function<InterViewRequest, String> changeInterView() {
        return request -> {
            resumeService.changeInterview(request.name(),request.evaluate());
            return "";
        };
    }
}
