package com.gr.geias.repository;

import com.gr.geias.model.ResumeDelivery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 简历投递记录数据访问接口
 */
@Mapper
public interface ResumeDeliveryRepository {
    
    /**
     * 根据用户ID查询投递记录
     * @param userId 用户ID
     * @return 投递记录列表
     */
    List<ResumeDelivery> findByUserId(Long userId);
    
    /**
     * 根据企业ID查询投递记录
     * @param companyId 企业ID
     * @return 投递记录列表
     */
    List<ResumeDelivery> findByCompanyId(Integer companyId);
    
    /**
     * 根据岗位ID查询投递记录
     * @param positionId 岗位ID
     * @return 投递记录列表
     */
    List<ResumeDelivery> findByPositionId(Integer positionId);
    
    /**
     * 根据投递ID查询投递记录
     * @param deliveryId 投递ID
     * @return 投递记录对象
     */
    ResumeDelivery findByDeliveryId(Integer deliveryId);
    
    /**
     * 检查用户是否已投递到指定岗位
     * @param userId 用户ID
     * @param positionId 岗位ID
     * @return 记录数
     */
    int existsByUserIdAndPositionId(@Param("userId") Long userId, @Param("positionId") Integer positionId);
    
    /**
     * 保存投递记录
     * @param resumeDelivery 投递记录对象
     * @return 影响行数
     */
    int save(ResumeDelivery resumeDelivery);
    
    /**
     * 更新投递记录
     * @param resumeDelivery 投递记录对象
     * @return 影响行数
     */
    int update(ResumeDelivery resumeDelivery);
    
    /**
     * 删除投递记录
     * @param deliveryId 投递ID
     * @return 影响行数
     */
    int delete(Integer deliveryId);
} 