package com.gr.geias.repository;

import com.gr.geias.model.ResumeDelivery;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 简历投递记录数据访问接口
 */
@Mapper
public interface ResumeDeliveryRepository {
    
    /**
     * 保存简历投递记录
     */
    @Insert("INSERT INTO tb_resume_delivery(resume_id, job_id, company_id, status, delivery_time, update_time) " +
            "VALUES(#{resumeId}, #{jobId}, #{companyId}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "deliveryId")
    int saveResumeDelivery(ResumeDelivery resumeDelivery);
    
    /**
     * 更新简历投递状态
     */
    @Update("UPDATE tb_resume_delivery SET status = #{status}, feedback = #{feedback}, update_time = NOW() " +
            "WHERE delivery_id = #{deliveryId}")
    int updateResumeDeliveryStatus(ResumeDelivery resumeDelivery);
    
    /**
     * 根据ID查询投递记录
     */
    @Select("SELECT * FROM tb_resume_delivery WHERE delivery_id = #{deliveryId}")
    ResumeDelivery findResumeDeliveryById(Integer deliveryId);
    
    /**
     * 根据简历ID和岗位ID查询投递记录
     */
    @Select("SELECT * FROM tb_resume_delivery WHERE resume_id = #{resumeId} AND job_id = #{jobId}")
    ResumeDelivery findResumeDeliveryByResumeAndJob(@Param("resumeId") Integer resumeId, @Param("jobId") Integer jobId);
    
    /**
     * 查询简历的所有投递记录
     */
    @Select("SELECT * FROM tb_resume_delivery WHERE resume_id = #{resumeId} ORDER BY delivery_time DESC")
    List<ResumeDelivery> findResumeDeliveriesByResumeId(Integer resumeId);
    
    /**
     * 查询用户的所有投递记录
     */
    @Select("SELECT rd.* FROM tb_resume_delivery rd " +
            "JOIN tb_resume r ON rd.resume_id = r.resume_id " +
            "WHERE r.user_id = #{userId} ORDER BY rd.delivery_time DESC")
    List<ResumeDelivery> findResumeDeliveriesByUserId(Long userId);
    
    /**
     * 查询岗位的所有投递记录
     */
    @Select("SELECT * FROM tb_resume_delivery WHERE job_id = #{jobId} ORDER BY delivery_time DESC")
    List<ResumeDelivery> findResumeDeliveriesByJobId(Integer jobId);
    
    /**
     * 查询企业的所有投递记录
     */
    @Select("SELECT * FROM tb_resume_delivery WHERE company_id = #{companyId} ORDER BY delivery_time DESC")
    List<ResumeDelivery> findResumeDeliveriesByCompanyId(Integer companyId);
    
    /**
     * 统计岗位的投递数量
     */
    @Select("SELECT COUNT(*) FROM tb_resume_delivery WHERE job_id = #{jobId}")
    int countResumeDeliveriesByJobId(Integer jobId);
    
    /**
     * 统计企业的投递数量
     */
    @Select("SELECT COUNT(*) FROM tb_resume_delivery WHERE company_id = #{companyId}")
    int countResumeDeliveriesByCompanyId(Integer companyId);
    
    /**
     * 统计用户的投递数量
     */
    @Select("SELECT COUNT(*) FROM tb_resume_delivery rd " +
            "JOIN tb_resume r ON rd.resume_id = r.resume_id " +
            "WHERE r.user_id = #{userId}")
    int countResumeDeliveriesByUserId(Long userId);
} 