package com.guangge.Interview.controller;

import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.util.CommonResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("/resume")
public class ResumeRewriterController {

    private final ConsumerClient consumerClient;

    public ResumeRewriterController(ConsumerClient consumerClient) {
        this.consumerClient = consumerClient;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<String> uploadResume(@RequestParam("resume") MultipartFile resumeFile,
                                             @RequestParam("jd") String jdText) {
        return this.consumerClient.uploadResume(resumeFile,jdText);
    }
}
