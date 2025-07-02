package com.gr.geias.repository;

import com.gr.geias.model.Resume;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 简历数据访问接口
 */
@Mapper
public interface ResumeRepository {
    
    /**
     * 根据用户ID查询所有简历
     * @param userId 用户ID
     * @return 简历列表
     */
    List<Resume> findByUserId(Long userId);
    
    /**
     * 根据简历ID查询简历
     * @param resumeId 简历ID
     * @return 简历对象
     */
    Resume findByResumeId(Integer resumeId);
    
    /**
     * 获取用户的默认简历
     * @param userId 用户ID
     * @return 默认简历
     */
    Resume findDefaultResumeByUserId(Long userId);
    
    /**
     * 保存简历
     * @param resume 简历对象
     * @return 影响行数
     */
    int save(Resume resume);
    
    /**
     * 更新简历
     * @param resume 简历对象
     * @return 影响行数
     */
    int update(Resume resume);
    
    /**
     * 删除简历
     * @param resumeId 简历ID
     * @return 影响行数
     */
    int delete(Integer resumeId);
    
    /**
     * 重置用户所有简历的默认状态
     * @param userId 用户ID
     * @return 影响行数
     */
    int resetDefaultResumes(Long userId);
} 