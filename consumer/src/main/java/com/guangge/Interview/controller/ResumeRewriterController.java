package com.guangge.Interview.controller;

import com.guangge.Interview.services.ResumeRewriterService;
import com.guangge.Interview.util.CommonResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("/resume")
public class ResumeRewriterController {

    private final ResumeRewriterService resumeRewriterService;

    public ResumeRewriterController(ResumeRewriterService resumeRewriterService) {
        this.resumeRewriterService = resumeRewriterService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<String> uploadResume(@RequestPart("resume") MultipartFile resumeFile,
                                             @RequestParam("jd") String jdText) throws Exception {
        String result = resumeRewriterService.processResume(resumeFile, jdText);
        return CommonResult.success(result);
    }
}
