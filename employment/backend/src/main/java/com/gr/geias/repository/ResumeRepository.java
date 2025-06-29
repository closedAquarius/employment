package com.gr.geias.repository;

import com.gr.geias.model.Resume;
import com.gr.geias.model.ResumeDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 简历数据访问接口
 */
@Mapper
public interface ResumeRepository {
    
    /**
     * 保存简历基本信息
     */
    @Insert("INSERT INTO tb_resume(user_id, resume_name, is_default, status, create_time, update_time) " +
            "VALUES(#{userId}, #{resumeName}, #{isDefault}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "resumeId")
    int saveResume(Resume resume);
    
    /**
     * 更新简历基本信息
     */
    @Update("UPDATE tb_resume SET resume_name = #{resumeName}, is_default = #{isDefault}, " +
            "status = #{status}, update_time = NOW() WHERE resume_id = #{resumeId}")
    int updateResume(Resume resume);
    
    /**
     * 删除简历
     */
    @Delete("DELETE FROM tb_resume WHERE resume_id = #{resumeId}")
    int deleteResume(Integer resumeId);
    
    /**
     * 根据ID查询简历
     */
    @Select("SELECT * FROM tb_resume WHERE resume_id = #{resumeId}")
    Resume findResumeById(Integer resumeId);
    
    /**
     * 查询用户的所有简历
     */
    @Select("SELECT * FROM tb_resume WHERE user_id = #{userId} ORDER BY is_default DESC, update_time DESC")
    List<Resume> findResumesByUserId(Long userId);
    
    /**
     * 查询用户的默认简历
     */
    @Select("SELECT * FROM tb_resume WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Resume findDefaultResumeByUserId(Long userId);
    
    /**
     * 保存简历详细内容
     */
    @Insert("INSERT INTO tb_resume_detail(resume_id, basic_info, education, work_experience, " +
            "project_experience, skills, certificates, self_evaluation, attachment_url) " +
            "VALUES(#{resumeId}, #{basicInfo}, #{education}, #{workExperience}, " +
            "#{projectExperience}, #{skills}, #{certificates}, #{selfEvaluation}, #{attachmentUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "detailId")
    int saveResumeDetail(ResumeDetail resumeDetail);
    
    /**
     * 更新简历详细内容
     */
    @Update("UPDATE tb_resume_detail SET basic_info = #{basicInfo}, education = #{education}, " +
            "work_experience = #{workExperience}, project_experience = #{projectExperience}, " +
            "skills = #{skills}, certificates = #{certificates}, self_evaluation = #{selfEvaluation}, " +
            "attachment_url = #{attachmentUrl} WHERE resume_id = #{resumeId}")
    int updateResumeDetail(ResumeDetail resumeDetail);
    
    /**
     * 根据简历ID查询详细内容
     */
    @Select("SELECT * FROM tb_resume_detail WHERE resume_id = #{resumeId}")
    ResumeDetail findResumeDetailByResumeId(Integer resumeId);
    
    /**
     * 取消用户所有简历的默认状态
     */
    @Update("UPDATE tb_resume SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefaultStatus(Long userId);
    
    /**
     * 设置简历为默认简历
     */
    @Update("UPDATE tb_resume SET is_default = 1 WHERE resume_id = #{resumeId}")
    int setDefaultResume(Integer resumeId);
} 