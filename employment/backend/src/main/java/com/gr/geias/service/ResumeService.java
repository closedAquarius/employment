package com.gr.geias.service;

import com.gr.geias.dto.ResumeDTO;
import com.gr.geias.model.Resume;

import java.util.List;

/**
 * 简历服务接口
 */
public interface ResumeService {
    
    /**
     * 创建简历
     */
    ResumeDTO createResume(ResumeDTO resumeDTO);
    
    /**
     * 更新简历
     */
    ResumeDTO updateResume(ResumeDTO resumeDTO);
    
    /**
     * 删除简历
     */
    boolean deleteResume(Integer resumeId, Long userId);
    
    /**
     * 获取简历详情
     */
    ResumeDTO getResumeById(Integer resumeId);
    
    /**
     * 获取用户的所有简历
     */
    List<Resume> getResumesByUserId(Long userId);
    
    /**
     * 获取用户的默认简历
     */
    Resume getDefaultResumeByUserId(Long userId);
    
    /**
     * 设置默认简历
     */
    boolean setDefaultResume(Integer resumeId, Long userId);
    
    /**
     * 从AI-Interview系统导入简历
     */
    ResumeDTO importResumeFromAI(Long userId, String aiResumeId);
    
    /**
     * 生成标准化简历
     */
    ResumeDTO generateStandardResume(ResumeDTO resumeDTO);
    
    /**
     * 根据岗位要求优化简历
     */
    ResumeDTO optimizeResumeForJob(Integer resumeId, Integer jobId);
    
    /**
     * 上传简历附件
     */
    String uploadResumeAttachment(Integer resumeId, byte[] fileData, String fileName);
    
    /**
     * 下载简历附件
     */
    byte[] downloadResumeAttachment(Integer resumeId);
}