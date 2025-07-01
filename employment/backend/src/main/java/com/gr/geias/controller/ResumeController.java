package com.gr.geias.controller;

import com.gr.geias.dto.ResumeDTO;
import com.gr.geias.service.ResumeService;
import com.gr.geias.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 简历控制器
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 获取用户的所有简历
     * @param userId 用户ID
     * @return 简历列表
     */
    @GetMapping
    public Result<List<ResumeDTO>> getUserResumes(@RequestParam Long userId) {
        List<ResumeDTO> resumes = resumeService.getUserResumes(userId);
        return Result.ok(resumes);
    }

    /**
     * 获取简历详情
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return 简历详情
     */
    @GetMapping("/{resumeId}")
    public Result<ResumeDTO> getResumeDetail(
            @PathVariable Integer resumeId,
            @RequestParam Long userId) {
        ResumeDTO resume = resumeService.getResumeDetail(resumeId, userId);
        if (resume == null) {
            return Result.fail("简历不存在或无权访问");
        }
        return Result.ok(resume);
    }

    /**
     * 创建简历
     * @param resumeDTO 简历DTO
     * @param userId 用户ID
     * @return 创建的简历ID
     */
    @PostMapping
    public Result<Integer> createResume(
            @RequestBody ResumeDTO resumeDTO,
            @RequestParam Long userId) {
        Integer resumeId = resumeService.createResume(resumeDTO, userId);
        return Result.ok(resumeId);
    }

    /**
     * 更新简历
     * @param resumeId 简历ID
     * @param resumeDTO 简历DTO
     * @param userId 用户ID
     * @return 更新结果
     */
    @PutMapping("/{resumeId}")
    public Result<Boolean> updateResume(
            @PathVariable Integer resumeId,
            @RequestBody ResumeDTO resumeDTO,
            @RequestParam Long userId) {
        resumeDTO.setResumeId(resumeId);
        boolean success = resumeService.updateResume(resumeDTO, userId);
        if (success) {
            return Result.ok(true);
        } else {
            return Result.fail("更新失败，简历不存在或无权修改");
        }
    }

    /**
     * 删除简历
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{resumeId}")
    public Result<Boolean> deleteResume(
            @PathVariable Integer resumeId,
            @RequestParam Long userId) {
        boolean success = resumeService.deleteResume(resumeId, userId);
        if (success) {
            return Result.ok(true);
        } else {
            return Result.fail("删除失败，简历不存在或无权删除");
        }
    }

    /**
     * 设置默认简历
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return 设置结果
     */
    @PutMapping("/{resumeId}/default")
    public Result<Boolean> setDefaultResume(
            @PathVariable Integer resumeId,
            @RequestParam Long userId) {
        boolean success = resumeService.setDefaultResume(resumeId, userId);
        if (success) {
            return Result.ok(true);
        } else {
            return Result.fail("设置失败，简历不存在或无权设置");
        }
    }

    /**
     * 获取用户的默认简历
     * @param userId 用户ID
     * @return 默认简历
     */
    @GetMapping("/default")
    public Result<ResumeDTO> getDefaultResume(@RequestParam Long userId) {
        ResumeDTO resume = resumeService.getDefaultResume(userId);
        if (resume == null) {
            return Result.fail("默认简历不存在");
        }
        return Result.ok(resume);
    }

    /**
     * 从AI面试系统导入简历
     * @param aiResumeId AI面试系统中的简历ID
     * @param userId 用户ID
     * @return 导入的简历ID
     */
    @PostMapping("/import")
    public Result<Integer> importFromAI(
            @RequestParam Long aiResumeId,
            @RequestParam Long userId) {
        Integer resumeId = resumeService.importFromAI(aiResumeId, userId);
        if (resumeId == null) {
            return Result.fail("导入失败");
        }
        return Result.ok(resumeId);
    }

    /**
     * 为特定岗位优化简历
     * @param resumeId 简历ID
     * @param jobId 岗位ID
     * @param userId 用户ID
     * @return 优化后的简历内容
     */
    @GetMapping("/{resumeId}/optimize")
    public Result<String> optimizeForJob(
            @PathVariable Integer resumeId,
            @RequestParam Integer jobId,
            @RequestParam Long userId) {
        String optimizedContent = resumeService.optimizeForJob(resumeId, jobId, userId);
        if (optimizedContent == null) {
            return Result.fail("优化失败");
        }
        return Result.ok(optimizedContent);
    }

    /**
     * 上传简历附件
     * @param resumeId 简历ID
     * @param file 附件文件
     * @param userId 用户ID
     * @return 附件URL
     */
    @PostMapping(value = "/{resumeId}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadAttachment(
            @PathVariable Integer resumeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId) {
        try {
            String fileUrl = resumeService.uploadAttachment(resumeId, file.getBytes(), file.getOriginalFilename(), userId);
            if (fileUrl == null) {
                return Result.fail("上传失败");
            }
            return Result.ok(fileUrl);
        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }

    /**
     * 投递简历
     * @param resumeId 简历ID
     * @param jobId 岗位ID
     * @param userId 用户ID
     * @return 投递结果
     */
    @PostMapping("/{resumeId}/deliver")
    public Result<Boolean> deliverResume(
            @PathVariable Integer resumeId,
            @RequestParam Integer jobId,
            @RequestParam Long userId) {
        boolean success = resumeService.deliverResume(resumeId, jobId, userId);
        if (success) {
            return Result.ok(true);
        } else {
            return Result.fail("投递失败，简历不存在、无权投递或已投递过该岗位");
        }
    }

    /**
     * 获取用户的简历投递记录
     * @param userId 用户ID
     * @return 投递记录列表
     */
    @GetMapping("/delivery-records")
    public Result<List<ResumeDTO>> getUserDeliveryRecords(@RequestParam Long userId) {
        List<ResumeDTO> records = resumeService.getUserDeliveryRecords(userId);
        return Result.ok(records);
    }
} 