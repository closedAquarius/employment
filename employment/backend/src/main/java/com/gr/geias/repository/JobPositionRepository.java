package com.gr.geias.repository;

import com.gr.geias.model.JobPosition;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 岗位信息数据访问接口
 */
@Mapper
public interface JobPositionRepository {
    
    /**
     * 保存岗位信息
     */
    @Insert("INSERT INTO tb_job_position(company_id, title, description, requirements, " +
            "salary_range, location, status, create_time, update_time) " +
            "VALUES(#{companyId}, #{title}, #{description}, #{requirements}, " +
            "#{salaryRange}, #{location}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "jobId")
    int saveJobPosition(JobPosition jobPosition);
    
    /**
     * 更新岗位信息
     */
    @Update("UPDATE tb_job_position SET title = #{title}, description = #{description}, " +
            "requirements = #{requirements}, salary_range = #{salaryRange}, location = #{location}, " +
            "status = #{status}, update_time = NOW() WHERE job_id = #{jobId} AND company_id = #{companyId}")
    int updateJobPosition(JobPosition jobPosition);
    
    /**
     * 删除岗位
     */
    @Delete("DELETE FROM tb_job_position WHERE job_id = #{jobId} AND company_id = #{companyId}")
    int deleteJobPosition(@Param("jobId") Integer jobId, @Param("companyId") Integer companyId);
    
    /**
     * 根据ID查询岗位
     */
    @Select("SELECT * FROM tb_job_position WHERE job_id = #{jobId}")
    JobPosition findJobPositionById(Integer jobId);
    
    /**
     * 查询企业的所有岗位
     */
    @Select("SELECT * FROM tb_job_position WHERE company_id = #{companyId} ORDER BY update_time DESC")
    List<JobPosition> findJobPositionsByCompanyId(Integer companyId);
    
    /**
     * 分页查询所有发布状态的岗位
     */
    @Select("SELECT * FROM tb_job_position WHERE status = 1 ORDER BY update_time DESC LIMIT #{offset}, #{limit}")
    List<JobPosition> findPublishedJobPositions(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 统计发布状态的岗位数量
     */
    @Select("SELECT COUNT(*) FROM tb_job_position WHERE status = 1")
    int countPublishedJobPositions();
    
    /**
     * 根据关键词搜索岗位
     */
    @Select("SELECT * FROM tb_job_position WHERE status = 1 AND " +
            "(title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY update_time DESC LIMIT #{offset}, #{limit}")
    List<JobPosition> searchJobPositions(@Param("keyword") String keyword, 
                                        @Param("offset") Integer offset, 
                                        @Param("limit") Integer limit);
    
    /**
     * 统计搜索结果数量
     */
    @Select("SELECT COUNT(*) FROM tb_job_position WHERE status = 1 AND " +
            "(title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))")
    int countSearchJobPositions(@Param("keyword") String keyword);
    
    /**
     * 更新岗位状态
     */
    @Update("UPDATE tb_job_position SET status = #{status}, update_time = NOW() " +
            "WHERE job_id = #{jobId} AND company_id = #{companyId}")
    int updateJobPositionStatus(@Param("jobId") Integer jobId, 
                               @Param("companyId") Integer companyId, 
                               @Param("status") Integer status);
} 