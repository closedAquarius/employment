package com.gr.geias.service;

import com.gr.geias.dto.ResumeDTO;

import java.util.List;

/**
 * 简历服务接口
 */
public interface ResumeService {
    
    /**
     * 获取用户的所有简历
     * @param userId 用户ID
     * @return 简历DTO列表
     */
    List<ResumeDTO> getUserResumes(Long userId);
    
    /**
     * 获取简历详情
     * @param resumeId 简历ID
     * @param userId 用户ID（用于权限验证）
     * @return 简历DTO对象
     */
    ResumeDTO getResumeDetail(Integer resumeId, Long userId);
    
    /**
     * 创建简历
     * @param resumeDTO 简历DTO对象
     * @param userId 用户ID
     * @return 创建的简历ID
     */
    Integer createResume(ResumeDTO resumeDTO, Long userId);
    
    /**
     * 更新简历
     * @param resumeDTO 简历DTO对象
     * @param userId 用户ID（用于权限验证）
     * @return 是否更新成功
     */
    boolean updateResume(ResumeDTO resumeDTO, Long userId);
    
    /**
     * 删除简历
     * @param resumeId 简历ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     */
    boolean deleteResume(Integer resumeId, Long userId);
    
    /**
     * 设置默认简历
     * @param resumeId 简历ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否设置成功
     */
    boolean setDefaultResume(Integer resumeId, Long userId);
    
    /**
     * 获取用户的默认简历
     * @param userId 用户ID
     * @return 默认简历DTO对象，如果不存在则返回null
     */
    ResumeDTO getDefaultResume(Long userId);
    
    /**
     * 从AI面试系统导入简历
     * @param aiResumeId AI面试系统中的简历ID
     * @param userId 用户ID
     * @return 导入的简历ID
     */
    Integer importFromAI(Long aiResumeId, Long userId);
    
    /**
     * 为特定岗位优化简历
     * @param resumeId 简历ID
     * @param jobId 岗位ID
     * @param userId 用户ID（用于权限验证）
     * @return 优化后的简历内容
     */
    String optimizeForJob(Integer resumeId, Integer jobId, Long userId);
    
    /**
     * 上传简历附件
     * @param resumeId 简历ID
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param userId 用户ID（用于权限验证）
     * @return 附件URL
     */
    String uploadAttachment(Integer resumeId, byte[] fileBytes, String fileName, Long userId);
    
    /**
     * 投递简历
     * @param resumeId 简历ID
     * @param jobId 岗位ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否投递成功
     */
    boolean deliverResume(Integer resumeId, Integer jobId, Long userId);
    
    /**
     * 获取用户的简历投递记录
     * @param userId 用户ID
     * @return 投递记录列表
     */
    List<ResumeDTO> getUserDeliveryRecords(Long userId);
} 