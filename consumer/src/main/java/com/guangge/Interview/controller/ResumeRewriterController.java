package com.guangge.Interview.controller;

import com.guangge.Interview.services.ResumeRewriterService;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.CvRequest;
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

    /**
     * 简历完善
     * @param resumeFile 简历文件
     * @param jdText 招聘JD
     * @return 修改后文件名
     * @throws Exception
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<String> uploadResume(@RequestPart("resume") MultipartFile resumeFile,
                                             @RequestParam("jd") String jdText) throws Exception {
        String result = resumeRewriterService.processResume(resumeFile, jdText);
        return CommonResult.success(result);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> createCv(@RequestBody CvRequest cvRequest) throws Exception {
        String result = resumeRewriterService.createResume(cvRequest);
        return CommonResult.success(result);
    }
}
