package com.guangge.Interview.controller;

import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.record.EducationRecord;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.CvRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

    @PostMapping(value = "/create")
    public CommonResult<String> createCv(@RequestBody CvRequest cvRequest) {
        return this.consumerClient.createCv(cvRequest);
    }
}
