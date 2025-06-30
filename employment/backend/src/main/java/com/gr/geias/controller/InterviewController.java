package com.gr.geias.controller;

import com.gr.geias.model.Interview;
import com.gr.geias.service.InterviewService;
import com.gr.geias.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 面试控制器
 */
@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    /**
     * 获取待处理的面试状态更新
     */
    @GetMapping("/status/pending")
    public Result<List<Map<String, Object>>> getPendingStatusUpdates(@RequestHeader("X-API-KEY") String apiKey) {
        // 验证API密钥
        if (!interviewService.validateApiKey(apiKey)) {
            return Result.fail(401, "无效的API密钥");
        }
        
        List<Map<String, Object>> pendingUpdates = interviewService.getPendingStatusUpdates();
        return Result.ok(pendingUpdates);
    }

    /**
     * 更新面试状态
     */
    @PostMapping("/status/update")
    public Result<Void> updateInterviewStatus(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestBody Map<String, Object> statusUpdate) {
        
        // 验证API密钥
        if (!interviewService.validateApiKey(apiKey)) {
            return Result.fail(401, "无效的API密钥");
        }
        
        Long candidateId = Long.valueOf(statusUpdate.get("candidateId").toString());
        Integer status = (Integer) statusUpdate.get("status");
        String remarks = (String) statusUpdate.get("remarks");
        
        boolean success = interviewService.updateInterviewStatus(candidateId, status, remarks);
        
        if (success) {
            return Result.ok("更新成功");
        } else {
            return Result.fail("更新面试状态失败");
        }
    }
    
    /**
     * 创建面试
     */
    @PostMapping
    public Result<Interview> createInterview(@RequestBody Interview interview) {
        Interview created = interviewService.createInterview(interview);
        return Result.ok(created);
    }
    
    /**
     * 获取面试详情
     */
    @GetMapping("/{interviewId}")
    public Result<Interview> getInterview(@PathVariable Long interviewId) {
        Interview interview = interviewService.getInterviewById(interviewId);
        if (interview != null) {
            return Result.ok(interview);
        } else {
            return Result.fail("面试不存在");
        }
    }
    
    /**
     * 获取简历的面试列表
     */
    @GetMapping("/resume/{resumeId}")
    public Result<List<Interview>> getInterviewsByResume(@PathVariable Long resumeId) {
        List<Interview> interviews = interviewService.getInterviewsByResumeId(resumeId);
        return Result.ok(interviews);
    }
    
    /**
     * 获取岗位的面试列表
     */
    @GetMapping("/position/{positionId}")
    public Result<List<Interview>> getInterviewsByPosition(@PathVariable Long positionId) {
        List<Interview> interviews = interviewService.getInterviewsByPositionId(positionId);
        return Result.ok(interviews);
    }
} 