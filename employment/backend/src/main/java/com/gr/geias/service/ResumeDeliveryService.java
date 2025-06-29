package com.gr.geias.service;

import com.gr.geias.model.ResumeDelivery;

import java.util.List;
import java.util.Map;

/**
 * 简历投递服务接口
 */
public interface ResumeDeliveryService {

    // 投递简历
    ResumeDelivery deliverResume(Long resumeId, Long jobId);
    
    // 获取用户的投递记录
    List<ResumeDelivery> getUserDeliveries(Long userId);
    
    // 获取企业收到的简历
    List<ResumeDelivery> getCompanyDeliveries(Long companyId);
    
    // 更新简历投递状态
    ResumeDelivery updateDeliveryStatus(Long deliveryId, Integer status, String feedback);
    
    // 统计投递成功率
    Map<String, Object> getDeliveryStatistics(Long userId);
}