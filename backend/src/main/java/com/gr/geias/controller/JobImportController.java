package com.gr.geias.controller;

import com.gr.geias.model.JobPosting;
import com.gr.geias.service.JobImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/jobs")
public class JobImportController {
    @Autowired
    JobImportService jobImportService;
    /**
     * 导入职位信息
     * @param classpathLocation JSON 文件路径
     * @return 导入结果
     */
    @PostMapping("/import")
    public ResponseEntity<String> importJobPostings(@RequestParam String classpathLocation) {
        try {
            jobImportService.importFromJson(classpathLocation);
            return ResponseEntity.ok("Job postings imported successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to import job postings: " + e.getMessage());
        }
    }
}