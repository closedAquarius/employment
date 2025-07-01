package com.gr.geias.controller;

import com.gr.geias.dto.JobFairAppliedDTO;
import com.gr.geias.dto.JobFairAvailableDTO;
import com.gr.geias.dto.JobFairWithCompaniesDTO;
import com.gr.geias.model.JobFair;
import com.gr.geias.model.JobFairCompany;
import com.gr.geias.service.JobFairCompanyService;
import com.gr.geias.service.JobFairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/fairs")
public class FairController {
    @Autowired
    private JobFairService jobFairService;

    @Autowired
    private JobFairCompanyService jobFairCompanyService;

    /**
     * 管理员可访问：招聘会 + 公司信息
     */
    @GetMapping("/JobFairWithCompanies")
    public ResponseEntity<List<JobFairWithCompaniesDTO>> getAllJobFairs() {
        return ResponseEntity.ok(jobFairService.getAllJobFairsWithCompanies());
    }

    /**
     * 管理员可访问：发布招聘会
     */
    @PostMapping("/jobfair")
    public ResponseEntity<String> addJobFair(@RequestBody JobFair jobFair) {
        jobFair.setCreateTime(new Date());
        jobFairService.insertJobFair(jobFair);
        return ResponseEntity.ok("招聘会创建成功");
    }

    /**
     * 管理员可访问：查看所有招聘会 + 当前占用展位编号
     */
    @GetMapping("/jobfairsWithboothstatus")
    public ResponseEntity<List<JobFairAvailableDTO>> getJobFairBoothStatus() {
        return ResponseEntity.ok(jobFairService.getJobFairsWithBoothStatus());
    }

    /**
     * 公司可访问：查看未申请的招聘会 + 剩余展位编号
     */
    @GetMapping("/jobfairsUnapplied")
    public ResponseEntity<List<JobFairAvailableDTO>> getUnappliedJobFairs(@RequestParam Integer companyId) {
        List<JobFairAvailableDTO> result = jobFairService.getUnappliedJobFairsWithBooths(companyId);
        return ResponseEntity.ok(result);
    }

    /**
     * 公司可访问：查看已申请的招聘会（含状态与展位号）
     */
    @GetMapping("/company/jobfairs/applied")
    public ResponseEntity<List<JobFairAppliedDTO>> getAppliedJobFairs(@RequestParam Integer companyId) {
        return ResponseEntity.ok(jobFairService.getAppliedJobFairs(companyId));
    }

    /**
     * 公司可访问：申请加入某场招聘会（仅在展位未满的情况下）
     */
    @PostMapping("/companyApply")
    public ResponseEntity<String> applyToJobFair(@RequestParam Integer jobFairId,
                                                 @RequestParam Integer companyId,
                                                 @RequestParam Integer boothLocation) {
        int approvedCount = jobFairService.countApprovedCompanies(jobFairId);
        int boothTotal = jobFairService.getBoothTotal(jobFairId);

        if (approvedCount >= boothTotal) {
            return ResponseEntity.badRequest().body("展位已满，无法申请");
        }

        if (jobFairCompanyService.existsApplication(jobFairId, companyId)) {
            return ResponseEntity.badRequest().body("您已申请过该招聘会");
        }

        JobFairCompany jfc = new JobFairCompany();
        jfc.setJobFairId(jobFairId);
        jfc.setCompanyId(companyId);
        jfc.setBoothLocation(boothLocation);
        jfc.setStatus(0); // 0-待审批

        jobFairCompanyService.applyToJobFair(jfc);
        return ResponseEntity.ok("申请已提交，等待管理员审核");
    }

    /**
     * 管理员可访问：审批公司加入申请（status=1 通过，2 拒绝）
     */
    @PostMapping("/adminReview")
    public ResponseEntity<String> reviewApplication(@RequestParam Integer jobFairId,
                                                    @RequestParam Integer companyId,
                                                    @RequestParam Integer status) {
        if (status != 1 && status != 2) {
            return ResponseEntity.badRequest().body("非法状态值，仅允许 1（通过）或 2（拒绝）");
        }

        if (status == 1) {
            int approvedCount = jobFairService.countApprovedCompanies(jobFairId);
            int boothTotal = jobFairService.getBoothTotal(jobFairId);
            if (approvedCount >= boothTotal) {
                return ResponseEntity.badRequest().body("展位已满，无法审批通过");
            }
        }

        jobFairCompanyService.reviewApplication(jobFairId, companyId, status);
        return ResponseEntity.ok("审批完成");
    }
    
}
