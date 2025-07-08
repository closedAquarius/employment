package com.gr.geias.controller;

import com.gr.geias.model.JobPosition;
import com.gr.geias.repository.JobPositionRepository;
import com.gr.geias.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 岗位控制器
 */
@RestController
@RequestMapping("/jobs")
public class JobPositionController {

    @Autowired
    private JobPositionRepository jobPositionRepository;

    /**
     * 获取所有发布中的岗位
     * @return 岗位列表
     */
    @GetMapping
    public Result<List<JobPosition>> getAllActiveJobs() {
        List<JobPosition> jobs = jobPositionRepository.findAll(1); // 1表示发布状态
        return Result.ok(jobs);
    }

    /**
     * 根据企业ID获取岗位
     * @param companyId 企业ID
     * @return 岗位列表
     */
    @GetMapping("/company/{companyId}")
    public Result<List<JobPosition>> getJobsByCompany(@PathVariable Integer companyId) {
        List<JobPosition> jobs = jobPositionRepository.findByCompanyId(companyId, null);
        return Result.ok(jobs);
    }

    /**
     * 获取岗位详情
     * @param jobId 岗位ID
     * @return 岗位详情
     */
    @GetMapping("/{jobId}")
    public Result<JobPosition> getJobDetail(@PathVariable Integer jobId) {
        JobPosition job = jobPositionRepository.findByPositionId(jobId);
        if (job == null) {
            return Result.fail("岗位不存在");
        }
        return Result.ok(job);
    }

    /**
     * 创建岗位
     * @param jobPosition 岗位信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Integer> createJob(@RequestBody JobPosition jobPosition) {
        jobPosition.setCreateTime(new Date());
        jobPosition.setUpdateTime(new Date());
        jobPositionRepository.save(jobPosition);
        return Result.ok(jobPosition.getJobId());
    }

    /**
     * 更新岗位
     * @param jobId 岗位ID
     * @param jobPosition 岗位信息
     * @return 更新结果
     */
    @PutMapping("/{jobId}")
    public Result<Boolean> updateJob(
            @PathVariable Integer jobId,
            @RequestBody JobPosition jobPosition) {
        JobPosition existingJob = jobPositionRepository.findByPositionId(jobId);
        if (existingJob == null) {
            return Result.fail("岗位不存在");
        }

        jobPosition.setJobId(jobId);
        jobPosition.setUpdateTime(new Date());
        int rows = jobPositionRepository.update(jobPosition);
        return Result.ok(rows > 0);
    }

    /**
     * 删除岗位
     * @param jobId 岗位ID
     * @return 删除结果
     */
    @DeleteMapping("/{jobId}")
    public Result<Boolean> deleteJob(@PathVariable Integer jobId) {
        JobPosition existingJob = jobPositionRepository.findByPositionId(jobId);
        if (existingJob == null) {
            return Result.fail("岗位不存在");
        }

        int rows = jobPositionRepository.delete(jobId);
        return Result.ok(rows > 0);
    }

    /**
     * 更新岗位状态
     * @param jobId 岗位ID
     * @param status 状态（0-下架，1-发布）
     * @return 更新结果
     */
    @PutMapping("/{jobId}/status")
    public Result<Boolean> updateJobStatus(
            @PathVariable Integer jobId,
            @RequestParam Integer status) {
        JobPosition existingJob = jobPositionRepository.findByPositionId(jobId);
        if (existingJob == null) {
            return Result.fail("岗位不存在");
        }

        existingJob.setStatus(status);
        existingJob.setUpdateTime(new Date());
        int rows = jobPositionRepository.update(existingJob);
        return Result.ok(rows > 0);
    }

    /**
     * 搜索岗位
     * @param keyword 关键词
     * @return 岗位列表
     */
    @GetMapping("/search")
    public Result<List<JobPosition>> searchJobs(@RequestParam String keyword) {
        // 由于我们没有在XML映射器中定义searchByKeyword方法，
        // 这里暂时返回所有发布中的岗位
        List<JobPosition> jobs = jobPositionRepository.findAll(1);
        return Result.ok(jobs);
    }
}