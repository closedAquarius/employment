package com.gr.geias.service;

import com.gr.geias.model.Interview;

import java.util.List;
import java.util.Map;

/**
 * 面试服务接口
 */
public interface InterviewService {
    
    /**
     * 创建面试
     * @param interview 面试信息
     * @return 创建后的面试信息
     */
    Interview createInterview(Interview interview);
    
    /**
     * 更新面试
     * @param interview 面试信息
     * @return 是否更新成功
     */
    boolean updateInterview(Interview interview);
    
    /**
     * 根据ID获取面试
     * @param interviewId 面试ID
     * @return 面试信息
     */
    Interview getInterviewById(Long interviewId);
    
    /**
     * 根据简历ID获取面试列表
     * @param resumeId 简历ID
     * @return 面试列表
     */
    List<Interview> getInterviewsByResumeId(Long resumeId);
    
    /**
     * 根据职位ID获取面试列表
     * @param positionId 职位ID
     * @return 面试列表
     */
    List<Interview> getInterviewsByPositionId(Long positionId);
    
    /**
     * 获取待同步的面试状态更新
     * @return 待同步的面试状态列表
     */
    List<Map<String, Object>> getPendingStatusUpdates();
    
    /**
     * 更新面试状态
     * @param candidateId 候选人ID
     * @param status 面试状态
     * @param remarks 面试备注
     * @return 是否更新成功
     */
    boolean updateInterviewStatus(Long candidateId, Integer status, String remarks);
    
    /**
     * 验证API密钥
     * @param apiKey API密钥
     * @return 是否有效
     */
    boolean validateApiKey(String apiKey);
} 