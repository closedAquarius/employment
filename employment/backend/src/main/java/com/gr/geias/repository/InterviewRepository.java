package com.gr.geias.repository;

import com.gr.geias.model.Interview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 面试数据访问接口
 */
@Mapper
public interface InterviewRepository {
    
    /**
     * 创建面试
     * @param interview 面试信息
     * @return 影响行数
     */
    int insert(Interview interview);
    
    /**
     * 更新面试
     * @param interview 面试信息
     * @return 影响行数
     */
    int update(Interview interview);
    
    /**
     * 根据ID查询面试
     * @param id 面试ID
     * @return 面试信息
     */
    Interview findById(@Param("id") Long id);
    
    /**
     * 根据简历ID查询面试列表
     * @param resumeId 简历ID
     * @return 面试列表
     */
    List<Interview> findByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据职位ID查询面试列表
     * @param positionId 职位ID
     * @return 面试列表
     */
    List<Interview> findByPositionId(@Param("positionId") Long positionId);
    
    /**
     * 获取待同步的面试状态更新
     * @return 待同步的面试状态列表
     */
    List<Map<String, Object>> findPendingStatusUpdates();
    
    /**
     * 更新面试状态
     * @param id 面试ID
     * @param status 面试状态
     * @param remarks 面试备注
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("remarks") String remarks);
    
    /**
     * 标记面试状态已同步
     * @param id 面试ID
     * @return 影响行数
     */
    int markStatusSynced(@Param("id") Long id);
} 