package com.gr.geias.service;

import com.gr.geias.model.OrganizationNum;

/**
 * 组织人数服务接口
 */
public interface OrganizationNumService {
    /**
     * 获取学院总人数
     * @param collegeId 学院ID
     * @return 人数
     */
    Integer getcollegeCount(Integer collegeId);

    /**
     * 获取专业人数
     * @param specialtyId 专业id
     * @return 人数
     */
    Integer getspecialtyCount(Integer specialtyId);

    /**
     * 获取班级人数
     * @param classId 班级ID
     * @return 人数
     */
    Integer getClassGradeCount(Integer classId);

    /**
     * 更新班级的人数
     * @param organizationNum 组织人数信息
     * @return 更新是否成功
     */
    Boolean updateClassGradesum(OrganizationNum organizationNum);

    /**
     * 获取学校总人数
     * @return 总人数
     */
    Integer geiAllCollegeSum();
}