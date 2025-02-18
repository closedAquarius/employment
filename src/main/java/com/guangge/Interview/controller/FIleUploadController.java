package com.guangge.Interview.controller;

import com.guangge.Interview.services.ResumeService;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("fileupload")
public class FIleUploadController {

    private final ResumeService resumeService;

    public FIleUploadController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @SneakyThrows
    @PostMapping("/resume/upload")
    public ResponseEntity<String> uploadJavaDoc(@RequestParam("id") String number, @RequestParam MultipartFile file) {
        long id = Long.valueOf(number);
        resumeService.saveResume(id,file);
        return ResponseEntity.ok().body("uploaded");
    }
}
