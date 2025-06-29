package com.gr.geias.controller;

import com.gr.geias.model.JobPosition;
import com.gr.geias.repository.JobPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 岗位控制器
 */
@RestController
@RequestMapping("/job")
public class JobPositionController {

    @Autowired
    private JobPositionRepository jobPositionRepository;

    /**
     * 发布岗位
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createJobPosition(@RequestBody JobPosition jobPosition,
                                                               @RequestHeader("X-User-Id") Long userId,
                                                               @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有企业HR才能发布岗位
        if (userType != 2) {
            result.put("success", false);
            result.put("message", "只有企业HR才能发布岗位");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            // 设置岗位状态为发布
            jobPosition.setStatus(1);
            
            // 保存岗位信息
            jobPositionRepository.saveJobPosition(jobPosition);
            
            result.put("success", true);
            result.put("message", "岗位发布成功");
            result.put("data", jobPosition);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "岗位发布失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 更新岗位
     */
    @PutMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> updateJobPosition(@PathVariable Integer jobId,
                                                               @RequestBody JobPosition jobPosition,
                                                               @RequestHeader("X-User-Id") Long userId,
                                                               @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有企业HR才能更新岗位
        if (userType != 2) {
            result.put("success", false);
            result.put("message", "只有企业HR才能更新岗位");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            // 查询原岗位
            JobPosition originalJob = jobPositionRepository.findJobPositionById(jobId);
            if (originalJob == null) {
                result.put("success", false);
                result.put("message", "岗位不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            // 设置岗位ID和企业ID
            jobPosition.setJobId(jobId);
            
            // 更新岗位信息
            int updated = jobPositionRepository.updateJobPosition(jobPosition);
            if (updated > 0) {
                result.put("success", true);
                result.put("message", "岗位更新成功");
                result.put("data", jobPosition);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "岗位更新失败，可能无权限");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "岗位更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> deleteJobPosition(@PathVariable Integer jobId,
                                                               @RequestHeader("X-User-Id") Long userId,
                                                               @RequestHeader("X-User-Type") Integer userType,
                                                               @RequestParam Integer companyId) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有企业HR才能删除岗位
        if (userType != 2) {
            result.put("success", false);
            result.put("message", "只有企业HR才能删除岗位");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        try {
            // 删除岗位
            int deleted = jobPositionRepository.deleteJobPosition(jobId, companyId);
            if (deleted > 0) {
                result.put("success", true);
                result.put("message", "岗位删除成功");
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "岗位删除失败，可能无权限或岗位不存在");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "岗位删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取岗位详情
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobPosition(@PathVariable Integer jobId) {
        Map<String, Object> result = new HashMap<>();
        try {
            JobPosition jobPosition = jobPositionRepository.findJobPositionById(jobId);
            if (jobPosition == null) {
                result.put("success", false);
                result.put("message", "岗位不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            result.put("success", true);
            result.put("data", jobPosition);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取岗位详情失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 分页获取所有发布状态的岗位
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getJobPositionList(@RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 计算分页参数
            int offset = page * size;
            
            // 查询岗位列表
            List<JobPosition> jobPositions = jobPositionRepository.findPublishedJobPositions(offset, size);
            
            // 查询总数
            int total = jobPositionRepository.countPublishedJobPositions();
            
            result.put("success", true);
            result.put("data", jobPositions);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取岗位列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 搜索岗位
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchJobPositions(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 计算分页参数
            int offset = page * size;
            
            // 搜索岗位
            List<JobPosition> jobPositions = jobPositionRepository.searchJobPositions(keyword, offset, size);
            
            // 查询总数
            int total = jobPositionRepository.countSearchJobPositions(keyword);
            
            result.put("success", true);
            result.put("data", jobPositions);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "搜索岗位失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取企业的所有岗位
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getCompanyJobPositions(@PathVariable Integer companyId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<JobPosition> jobPositions = jobPositionRepository.findJobPositionsByCompanyId(companyId);
            
            result.put("success", true);
            result.put("data", jobPositions);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取企业岗位失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 更新岗位状态（上架/下架）
     */
    @PutMapping("/{jobId}/status")
    public ResponseEntity<Map<String, Object>> updateJobPositionStatus(@PathVariable Integer jobId,
                                                                     @RequestParam Integer status,
                                                                     @RequestParam Integer companyId,
                                                                     @RequestHeader("X-User-Id") Long userId,
                                                                     @RequestHeader("X-User-Type") Integer userType) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户类型，只有企业HR才能更新岗位状态
        if (userType != 2) {
            result.put("success", false);
            result.put("message", "只有企业HR才能更新岗位状态");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        
        // 检查状态值是否合法
        if (status != 0 && status != 1) {
            result.put("success", false);
            result.put("message", "状态值不合法，只能是0(下架)或1(发布)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        
        try {
            // 更新岗位状态
            int updated = jobPositionRepository.updateJobPositionStatus(jobId, companyId, status);
            if (updated > 0) {
                result.put("success", true);
                result.put("message", status == 1 ? "岗位已发布" : "岗位已下架");
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "更新岗位状态失败，可能无权限或岗位不存在");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新岗位状态失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
} 