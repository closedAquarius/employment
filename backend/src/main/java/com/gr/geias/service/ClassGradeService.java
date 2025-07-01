package com.gr.geias.service;

import com.gr.geias.model.ClassGrade;

import java.util.List;

/**
 * 班级信息服务接口
 */
public interface ClassGradeService {

    /**
     * 获取班级
     * 
     * @param specialtyId 专业ID
     * @param adminId 管理员ID
     * @return 班级列表
     */
    List<ClassGrade> getClassGrade(Integer specialtyId, Integer adminId);

    /**
     * 添加班级和班级人数并统计到学院和专业人数上
     * 
     * @param classGrade 班级信息
     * @param sum 人数
     * @param collegeId 学院ID
     * @return 是否成功
     */
    Boolean addClassGrade(ClassGrade classGrade, Integer sum, Integer collegeId);

    /**
     * 修改班级
     * 
     * @param classGrade 班级信息
     * @param sum 人数
     * @param college 学院ID
     * @return 是否成功
     */
    Boolean updateClassGrade(ClassGrade classGrade, Integer sum, Integer college);

    /**
     * 删除班级信息
     * 
     * @param classId 班级ID
     * @return 是否成功
     */
    Boolean delClassGrade(Integer classId);

    int getClassGradeCount(Integer specialtyId);

    List<ClassGrade> getClassGradePage(Integer specialtyId, int offset, int pageSize);
} 