package com.gr.geias.controller;

import com.gr.geias.model.ResumeDelivery;
import com.gr.geias.repository.ResumeDeliveryRepository;
import com.gr.geias.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 简历投递控制器
 */
@RestController
@RequestMapping("/api/resume-deliveries")
public class ResumeDeliveryController {

    @Autowired
    private ResumeDeliveryRepository resumeDeliveryRepository;

    /**
     * 根据用户ID获取投递记录
     * @param userId 用户ID
     * @return 投递记录列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<ResumeDelivery>> getDeliveriesByUserId(@PathVariable Long userId) {
        List<ResumeDelivery> deliveries = resumeDeliveryRepository.findByUserId(userId);
        return Result.ok(deliveries);
    }

    /**
     * 根据岗位ID获取投递记录
     * @param positionId 岗位ID
     * @return 投递记录列表
     */
    @GetMapping("/position/{positionId}")
    public Result<List<ResumeDelivery>> getDeliveriesByPositionId(@PathVariable Integer positionId) {
        List<ResumeDelivery> deliveries = resumeDeliveryRepository.findByPositionId(positionId);
        return Result.ok(deliveries);
    }

    /**
     * 根据企业ID获取投递记录
     * @param companyId 企业ID
     * @return 投递记录列表
     */
    @GetMapping("/company/{companyId}")
    public Result<List<ResumeDelivery>> getDeliveriesByCompanyId(@PathVariable Integer companyId) {
        List<ResumeDelivery> deliveries = resumeDeliveryRepository.findByCompanyId(companyId);
        return Result.ok(deliveries);
    }

    /**
     * 获取投递记录详情
     * @param deliveryId 投递ID
     * @return 投递记录详情
     */
    @GetMapping("/{deliveryId}")
    public Result<ResumeDelivery> getDeliveryDetail(@PathVariable Integer deliveryId) {
        ResumeDelivery delivery = resumeDeliveryRepository.findByDeliveryId(deliveryId);
        if (delivery == null) {
            return Result.fail("投递记录不存在");
        }
        return Result.ok(delivery);
    }

    /**
     * 更新投递记录状态
     * @param deliveryId 投递ID
     * @param status 状态（0-待查看，1-已查看，2-邀请面试，3-不合适）
     * @param feedback 反馈内容
     * @return 更新结果
     */
    @PutMapping("/{deliveryId}/status")
    public Result<Boolean> updateDeliveryStatus(
            @PathVariable Integer deliveryId,
            @RequestParam Integer status,
            @RequestParam(required = false) String feedback) {
        ResumeDelivery delivery = resumeDeliveryRepository.findByDeliveryId(deliveryId);
        if (delivery == null) {
            return Result.fail("投递记录不存在");
        }
        
        delivery.setStatus(status);
        delivery.setFeedback(feedback);
        delivery.setUpdateTime(new Date());
        int rows = resumeDeliveryRepository.update(delivery);
        return Result.ok(rows > 0);
    }

    /**
     * 删除投递记录
     * @param deliveryId 投递ID
     * @return 删除结果
     */
    @DeleteMapping("/{deliveryId}")
    public Result<Boolean> deleteDelivery(@PathVariable Integer deliveryId) {
        ResumeDelivery delivery = resumeDeliveryRepository.findByDeliveryId(deliveryId);
        if (delivery == null) {
            return Result.fail("投递记录不存在");
        }
        
        int rows = resumeDeliveryRepository.delete(deliveryId);
        return Result.ok(rows > 0);
    }
} 