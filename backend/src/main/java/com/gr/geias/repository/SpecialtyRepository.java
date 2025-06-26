package com.gr.geias.repository;

import com.gr.geias.model.Specialty;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 专业信息Repository接口
 */
public interface SpecialtyRepository {

    /**
     * 获取专业信息
     * @param collegeId 学院
     * @return
     */
    List<Specialty> querySpecialty(@Param("collegeId") Integer collegeId);

    /**
     * 根据学院ID查询专业
     *
     * @param collegeId 学院ID
     * @return 专业列表
     */
    @Select("select * from tb_specialty where college_id = #{collegeId}")
    List<Specialty> querySpecialtyByCollegeId(@Param("collegeId") Integer collegeId);

    /**
     * 通过专业id 获取 学院id
     * @param specialtyId 专业id
     * @return
     */
    @Select("select college_id from tb_specialty where specialty_id=#{specialtyId}")
    Integer queryCollegeId(@Param("specialtyId") Integer specialtyId);

    /**
     * 添加专业信息
     * @param specialty
     * @return
     */
    Integer insertSpecialty(@Param("specialty") Specialty specialty);

    /**
     * 更新 专业
     * @param specialty 信息
     * @return
     */
    @Update("update tb_specialty set specialty_name=#{specialty.specialtyName},college_id=#{specialty.collegeId} where specialty_id=#{specialty.specialtyId}")
    Integer updateSpecialty(@Param("specialty")Specialty specialty);

    /**
     * 删除专业
     * @param specialtyId
     * @return
     */
    @Delete("delete from tb_specialty where specialty_id = #{specialtyId}")
    Integer delSpecialty(@Param("specialtyId") Integer specialtyId);

    /**
     * 查询专业信息通过id
     * @param specialtyId
     * @return
     */
    @Select("select * from tb_specialty where specialty_id = #{specialtyId}")
    Specialty querySpecialtyById(@Param("specialtyId") Integer specialtyId);

    /**
     * 获取专业总人数
     * @param specialtyId
     * @return
     */
    Integer queryCountSpecialty(@Param("specialtyId")Integer specialtyId);
} 