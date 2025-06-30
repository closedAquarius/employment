package com.gr.geias.service.impl;

import com.gr.geias.model.Interview;
import com.gr.geias.repository.InterviewRepository;
import com.gr.geias.service.InterviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 面试服务实现类
 */
@Service
public class InterviewServiceImpl implements InterviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(InterviewServiceImpl.class);
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Value("${employment.api.key:default-api-key}")
    private String apiKey;
    
    @Override
    @Transactional
    public Interview createInterview(Interview interview) {
        // 设置默认状态为待面试
        if (interview.getStatus() == null) {
            interview.setStatus(1);
        }
        
        interviewRepository.insert(interview);
        return getInterviewById(interview.getId());
    }
    
    @Override
    @Transactional
    public boolean updateInterview(Interview interview) {
        return interviewRepository.update(interview) > 0;
    }
    
    @Override
    public Interview getInterviewById(Long interviewId) {
        return interviewRepository.findById(interviewId);
    }
    
    @Override
    public List<Interview> getInterviewsByResumeId(Long resumeId) {
        return interviewRepository.findByResumeId(resumeId);
    }
    
    @Override
    public List<Interview> getInterviewsByPositionId(Long positionId) {
        return interviewRepository.findByPositionId(positionId);
    }
    
    @Override
    public List<Map<String, Object>> getPendingStatusUpdates() {
        return interviewRepository.findPendingStatusUpdates();
    }
    
    @Override
    @Transactional
    public boolean updateInterviewStatus(Long candidateId, Integer status, String remarks) {
        try {
            // 根据候选人ID查找最新的面试记录
            // 这里假设一个候选人可能有多个面试，我们更新最新的一个
            List<Map<String, Object>> interviews = interviewRepository.findPendingStatusUpdates();
            
            for (Map<String, Object> interview : interviews) {
                Long currentCandidateId = Long.valueOf(interview.get("candidateId").toString());
                if (candidateId.equals(currentCandidateId)) {
                    Long interviewId = Long.valueOf(interview.get("interviewId").toString());
                    interviewRepository.updateStatus(interviewId, status, remarks);
                    interviewRepository.markStatusSynced(interviewId);
                    logger.info("更新面试状态成功: interviewId={}, candidateId={}, status={}", interviewId, candidateId, status);
                    return true;
                }
            }
            
            logger.warn("未找到候选人对应的面试记录: candidateId={}", candidateId);
            return false;
        } catch (Exception e) {
            logger.error("更新面试状态失败: candidateId=" + candidateId, e);
            return false;
        }
    }
    
    @Override
    public boolean validateApiKey(String requestApiKey) {
        return apiKey.equals(requestApiKey);
    }
} 