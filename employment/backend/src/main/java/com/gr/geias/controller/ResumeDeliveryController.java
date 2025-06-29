package com.gr.geias.controller;

import com.gr.geias.model.ResumeDelivery;
import com.gr.geias.repository.ResumeDeliveryRepository;
import com.gr.geias.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简历投递控制器
 */
@RestController
@RequestMapping("/delivery")
public class ResumeDeliveryController {

    @Autowired
    private ResumeDeliveryRepository resumeDeliveryRepository;
    
    @Autowired
    private ResumeService resumeService;

    /**
     * 投递简历
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> deliverResume(@RequestBody ResumeDelivery resumeDelivery,
                                                           @RequestHeader("X-User-Id") Long userId,
                                                           @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有学生才能投递简历
        if (userType != 1) {
            result.put("success", false);
            result.put("message", "只有学生才能投递简历");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            // 检查简历所有权
            if (resumeService.getResumeById(resumeDelivery.getResumeId()) == null) {
                result.put("success", false);
                result.put("message", "简历不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            // 检查是否已经投递过
            ResumeDelivery existingDelivery = resumeDeliveryRepository.findResumeDeliveryByResumeAndJob(
                resumeDelivery.getResumeId(), 
                resumeDelivery.getJobId()
            );
            
            if (existingDelivery != null) {
                result.put("success", false);
                result.put("message", "您已经投递过该岗位，请勿重复投递");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
            // 设置投递状态为待查看
            resumeDelivery.setStatus(0);
            
            // 保存投递记录
            resumeDeliveryRepository.saveResumeDelivery(resumeDelivery);
            
            result.put("success", true);
            result.put("message", "简历投递成功");
            result.put("data", resumeDelivery);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "简历投递失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 更新投递状态（HR操作）
     */
    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<Map<String, Object>> updateDeliveryStatus(@PathVariable Integer deliveryId,
                                                                  @RequestBody ResumeDelivery resumeDelivery,
                                                                  @RequestHeader("X-User-Id") Long userId,
                                                                  @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有企业HR才能更新投递状态
        if (userType != 2) {
            result.put("success", false);
            result.put("message", "只有企业HR才能更新投递状态");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            // 查询原投递记录
            ResumeDelivery originalDelivery = resumeDeliveryRepository.findResumeDeliveryById(deliveryId);
            if (originalDelivery == null) {
                result.put("success", false);
                result.put("message", "投递记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            // 设置投递ID
            resumeDelivery.setDeliveryId(deliveryId);
            
            // 更新投递状态
            resumeDeliveryRepository.updateResumeDeliveryStatus(resumeDelivery);
            
            result.put("success", true);
            result.put("message", "投递状态更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "投递状态更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取投递详情
     */
    @GetMapping("/{deliveryId}")
    public ResponseEntity<Map<String, Object>> getDeliveryDetail(@PathVariable Integer deliveryId,
                                                               @RequestHeader("X-User-Id") Long userId,
                                                               @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        try {
            ResumeDelivery resumeDelivery = resumeDeliveryRepository.findResumeDeliveryById(deliveryId);
            if (resumeDelivery == null) {
                result.put("success", false);
                result.put("message", "投递记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            result.put("success", true);
            result.put("data", resumeDelivery);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取投递详情失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取用户的所有投递记录
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserDeliveries(@RequestHeader("X-User-Id") Long userId,
                                                               @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有学生才能查看自己的投递记录
        if (userType != 1) {
            result.put("success", false);
            result.put("message", "只有学生才能查看自己的投递记录");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            List<ResumeDelivery> deliveries = resumeDeliveryRepository.findResumeDeliveriesByUserId(userId);
            
            result.put("success", true);
            result.put("data", deliveries);
            result.put("total", resumeDeliveryRepository.countResumeDeliveriesByUserId(userId));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取投递记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取岗位的所有投递记录（HR查看）
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobDeliveries(@PathVariable Integer jobId,
                                                              @RequestHeader("X-User-Id") Long userId,
                                                              @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有企业HR才能查看岗位投递记录
        if (userType != 2) {
            result.put("success", false);
            result.put("message", "只有企业HR才能查看岗位投递记录");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            List<ResumeDelivery> deliveries = resumeDeliveryRepository.findResumeDeliveriesByJobId(jobId);
            
            result.put("success", true);
            result.put("data", deliveries);
            result.put("total", resumeDeliveryRepository.countResumeDeliveriesByJobId(jobId));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取岗位投递记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取企业的所有投递记录（HR查看）
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getCompanyDeliveries(@PathVariable Integer companyId,
                                                                  @RequestHeader("X-User-Id") Long userId,
                                                                  @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有企业HR才能查看企业投递记录
        if (userType != 2) {
            result.put("success", false);
            result.put("message", "只有企业HR才能查看企业投递记录");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            List<ResumeDelivery> deliveries = resumeDeliveryRepository.findResumeDeliveriesByCompanyId(companyId);
            
            result.put("success", true);
            result.put("data", deliveries);
            result.put("total", resumeDeliveryRepository.countResumeDeliveriesByCompanyId(companyId));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取企业投递记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
} 