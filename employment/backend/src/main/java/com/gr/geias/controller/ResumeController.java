package com.gr.geias.controller;

import com.gr.geias.dto.ResumeDTO;
import com.gr.geias.model.Resume;
import com.gr.geias.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简历控制器
 */
@RestController
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 创建简历
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createResume(@RequestBody ResumeDTO resumeDTO,
                                                          @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            resumeDTO.setUserId(userId);
            ResumeDTO createdResume = resumeService.createResume(resumeDTO);
            result.put("success", true);
            result.put("message", "创建简历成功");
            result.put("data", createdResume);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建简历失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 更新简历
     */
    @PutMapping("/{resumeId}")
    public ResponseEntity<Map<String, Object>> updateResume(@PathVariable Integer resumeId,
                                                          @RequestBody ResumeDTO resumeDTO,
                                                          @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            resumeDTO.setResumeId(resumeId);
            resumeDTO.setUserId(userId);
            ResumeDTO updatedResume = resumeService.updateResume(resumeDTO);
            result.put("success", true);
            result.put("message", "更新简历成功");
            result.put("data", updatedResume);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新简历失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取简历详情
     */
    @GetMapping("/{resumeId}")
    public ResponseEntity<Map<String, Object>> getResume(@PathVariable Integer resumeId,
                                                       @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            ResumeDTO resume = resumeService.getResumeById(resumeId);
            
            // 检查是否是简历所有者
            if (resume == null || !resume.getUserId().equals(userId)) {
                result.put("success", false);
                result.put("message", "无权访问该简历");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
            
            result.put("success", true);
            result.put("data", resume);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取简历失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取用户的所有简历
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getResumeList(@RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Resume> resumeList = resumeService.getResumesByUserId(userId);
            result.put("success", true);
            result.put("data", resumeList);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取简历列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 删除简历
     */
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Map<String, Object>> deleteResume(@PathVariable Integer resumeId,
                                                          @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = resumeService.deleteResume(resumeId, userId);
            if (success) {
                result.put("success", true);
                result.put("message", "删除简历成功");
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "删除简历失败，可能无权限或简历不存在");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除简历失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 设置默认简历
     */
    @PutMapping("/{resumeId}/default")
    public ResponseEntity<Map<String, Object>> setDefaultResume(@PathVariable Integer resumeId,
                                                              @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = resumeService.setDefaultResume(resumeId, userId);
            if (success) {
                result.put("success", true);
                result.put("message", "设置默认简历成功");
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "设置默认简历失败，可能无权限或简历不存在");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "设置默认简历失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 上传简历附件
     */
    @PostMapping("/{resumeId}/attachment")
    public ResponseEntity<Map<String, Object>> uploadAttachment(@PathVariable Integer resumeId,
                                                              @RequestParam("file") MultipartFile file,
                                                              @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查简历所有权
            ResumeDTO resume = resumeService.getResumeById(resumeId);
            if (resume == null || !resume.getUserId().equals(userId)) {
                result.put("success", false);
                result.put("message", "无权上传附件到该简历");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
            
            // 上传附件
            String attachmentUrl = resumeService.uploadResumeAttachment(
                resumeId, 
                file.getBytes(), 
                file.getOriginalFilename()
            );
            
            result.put("success", true);
            result.put("message", "上传附件成功");
            result.put("data", attachmentUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "上传附件失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 下载简历附件
     */
    @GetMapping("/{resumeId}/attachment")
    public ResponseEntity<?> downloadAttachment(@PathVariable Integer resumeId,
                                               @RequestHeader("X-User-Id") Long userId) {
        try {
            // 检查简历所有权
            ResumeDTO resume = resumeService.getResumeById(resumeId);
            if (resume == null || !resume.getUserId().equals(userId)) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "无权下载该简历附件");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
            
            // 下载附件
            byte[] fileData = resumeService.downloadResumeAttachment(resumeId);
            
            // 从URL中提取文件名
            String fileName = "resume_attachment.pdf";
            if (resume.getAttachmentUrl() != null && resume.getAttachmentUrl().contains("/")) {
                fileName = resume.getAttachmentUrl().substring(resume.getAttachmentUrl().lastIndexOf("/") + 1);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "下载附件失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 从AI-Interview系统导入简历
     */
    @PostMapping("/import-from-ai")
    public ResponseEntity<Map<String, Object>> importFromAI(@RequestParam String aiResumeId,
                                                          @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            ResumeDTO importedResume = resumeService.importResumeFromAI(userId, aiResumeId);
            result.put("success", true);
            result.put("message", "导入简历成功");
            result.put("data", importedResume);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "导入简历失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 生成标准化简历
     */
    @PostMapping("/generate-standard")
    public ResponseEntity<Map<String, Object>> generateStandard(@RequestBody ResumeDTO resumeDTO,
                                                              @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            resumeDTO.setUserId(userId);
            ResumeDTO generatedResume = resumeService.generateStandardResume(resumeDTO);
            result.put("success", true);
            result.put("message", "生成标准化简历成功");
            result.put("data", generatedResume);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成标准化简历失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 根据岗位要求优化简历
     */
    @PostMapping("/{resumeId}/optimize")
    public ResponseEntity<Map<String, Object>> optimizeForJob(@PathVariable Integer resumeId,
                                                            @RequestParam Integer jobId,
                                                            @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查简历所有权
            ResumeDTO resume = resumeService.getResumeById(resumeId);
            if (resume == null || !resume.getUserId().equals(userId)) {
                result.put("success", false);
                result.put("message", "无权优化该简历");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
            
            ResumeDTO optimizedResume = resumeService.optimizeResumeForJob(resumeId, jobId);
            result.put("success", true);
            result.put("message", "简历优化成功");
            result.put("data", optimizedResume);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "简历优化失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}