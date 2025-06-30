package com.guangge.Interview.services;

import com.guangge.Interview.data.CandidateStatus;
import com.guangge.Interview.data.CandidateStatusConverter;
import com.guangge.Interview.data.Candidates;
import com.guangge.Interview.repository.CandidatesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 面试状态同步服务，负责与就业系统进行面试状态的同步
 */
@Service
public class InterviewStatusSyncService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewStatusSyncService.class);

    @Autowired
    private CandidatesRepository candidatesRepository;

    @Autowired
    private CandidateStatusConverter statusConverter;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${employment.api.url:http://localhost:8080}")
    private String employmentApiUrl;

    @Value("${employment.api.key:}")
    private String apiKey;

    /**
     * 定时同步面试状态到就业系统
     * 每10分钟执行一次
     */
    @Scheduled(fixedRate = 600000)
    public void syncInterviewStatusToEmployment() {
        logger.info("开始同步面试状态到就业系统");
        
        try {
            // 获取所有候选人
            List<Candidates> candidates = candidatesRepository.findAll();
            
            for (Candidates candidate : candidates) {
                try {
                    // 转换状态
                    Integer employmentStatus = statusConverter.toEmploymentStatus(candidate.getStatus().getCode());
                    
                    // 构建请求参数
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("candidateId", candidate.getId());
                    requestBody.put("status", employmentStatus);
                    requestBody.put("remarks", candidate.getRemarks());
                    
                    // 设置请求头
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-API-KEY", apiKey);
                    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
                    
                    // 调用就业系统API
                    ResponseEntity<Map> response = restTemplate.exchange(
                        employmentApiUrl + "/api/interview/status/update", 
                        HttpMethod.POST, 
                        requestEntity, 
                        Map.class
                    );
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                        logger.info("成功同步候选人状态: ID={}, 状态={}", candidate.getId(), employmentStatus);
                    } else {
                        logger.error("同步候选人状态失败: ID={}, 响应码={}", candidate.getId(), response.getStatusCode());
                    }
                } catch (Exception e) {
                    logger.error("同步候选人状态异常: ID=" + candidate.getId(), e);
                }
            }
        } catch (Exception e) {
            logger.error("面试状态同步任务异常", e);
        }
    }
    
    /**
     * 从就业系统获取面试状态更新
     * 每10分钟执行一次
     */
    @Scheduled(fixedRate = 600000)
    public void syncInterviewStatusFromEmployment() {
        logger.info("开始从就业系统获取面试状态更新");
        
        try {
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", apiKey);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            // 调用就业系统API
            ResponseEntity<List> response = restTemplate.exchange(
                employmentApiUrl + "/api/interview/status/pending", 
                HttpMethod.GET, 
                requestEntity, 
                List.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> statusUpdates = response.getBody();
                
                for (Map<String, Object> update : statusUpdates) {
                    try {
                        Long candidateId = Long.valueOf(update.get("candidateId").toString());
                        Integer employmentStatus = (Integer) update.get("status");
                        
                        // 转换状态
                        Integer interviewStatus = statusConverter.toInterviewStatus(employmentStatus);
                        
                        // 更新候选人状态
                        Candidates candidate = candidatesRepository.findById(candidateId).orElse(null);
                        if (candidate != null) {
                            candidate.setStatus(CandidateStatus.fromCode(interviewStatus));
                            candidatesRepository.save(candidate);
                            logger.info("成功更新候选人状态: ID={}, 状态={}", candidateId, interviewStatus);
                        } else {
                            logger.warn("未找到候选人: ID={}", candidateId);
                        }
                    } catch (Exception e) {
                        logger.error("处理状态更新异常: " + update, e);
                    }
                }
            } else {
                logger.error("获取状态更新失败: 响应码={}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("获取面试状态更新任务异常", e);
        }
    }
} 