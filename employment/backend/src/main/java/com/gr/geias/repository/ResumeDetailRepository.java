package com.gr.geias.repository;

import com.gr.geias.model.ResumeDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 简历详情数据访问接口
 */
@Mapper
public interface ResumeDetailRepository {
    
    /**
     * 根据简历ID查询简历详情
     * @param resumeId 简历ID
     * @return 简历详情对象
     */
    ResumeDetail findByResumeId(Integer resumeId);
    
    /**
     * 保存简历详情
     * @param resumeDetail 简历详情对象
     * @return 影响行数
     */
    int save(ResumeDetail resumeDetail);
    
    /**
     * 更新简历详情
     * @param resumeDetail 简历详情对象
     * @return 影响行数
     */
    int update(ResumeDetail resumeDetail);
    
    /**
     * 删除简历详情
     * @param resumeId 简历ID
     * @return 影响行数
     */
    int deleteByResumeId(Integer resumeId);
} 