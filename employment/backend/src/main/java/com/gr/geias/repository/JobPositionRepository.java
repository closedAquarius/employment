package com.gr.geias.repository;

import com.gr.geias.model.JobPosition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位信息数据访问接口
 */
@Mapper
public interface JobPositionRepository {
    
    /**
     * 查询所有岗位
     * @param status 可选状态过滤
     * @return 岗位列表
     */
    List<JobPosition> findAll(@Param("status") Integer status);
    
    /**
     * 根据企业ID查询岗位
     * @param companyId 企业ID
     * @param status 可选状态过滤
     * @return 岗位列表
     */
    List<JobPosition> findByCompanyId(@Param("companyId") Integer companyId, @Param("status") Integer status);
    
    /**
     * 根据岗位ID查询岗位
     * @param jobId 岗位ID
     * @return 岗位对象
     */
    JobPosition findByPositionId(@Param("positionId") Integer jobId);
    
    /**
     * 保存岗位
     * @param jobPosition 岗位对象
     * @return 影响行数
     */
    int save(JobPosition jobPosition);
    
    /**
     * 更新岗位
     * @param jobPosition 岗位对象
     * @return 影响行数
     */
    int update(JobPosition jobPosition);
    
    /**
     * 删除岗位
     * @param jobId 岗位ID
     * @return 影响行数
     */
    int delete(@Param("positionId") Integer jobId);
} 