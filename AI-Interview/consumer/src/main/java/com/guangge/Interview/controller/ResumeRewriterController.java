package com.guangge.Interview.controller;

import com.guangge.Interview.services.ResumeRewriterService;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.CvRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    
    /**
     * 获取PDF文件
     * @param fileName PDF文件名
     * @return PDF文件
     * @throws IOException
     */
    @GetMapping(value = "/pdf/{fileName:.+}")
    public ResponseEntity<Resource> getPdf(@PathVariable String fileName) throws IOException {
        // 构建PDF文件的完整路径
        String pdfPath = "external/static/pdf/" + fileName;
        File file = new File(pdfPath);
        
        // 检查文件是否存在
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        // 创建文件资源
        Resource resource = new FileSystemResource(file);
        
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName);
        
        // 返回PDF文件
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(resource);
    }
}
