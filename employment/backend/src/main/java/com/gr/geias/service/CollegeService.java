package com.gr.geias.service;

import com.gr.geias.model.College;

import java.util.List;

/**
 * 学院信息服务接口
 */
public interface CollegeService {
    /**
     * 获取学院
     * 
     * @param adminId 管理员ID
     * @return 学院列表
     */
    List<College> getCollege(Integer adminId);

    /**
     * 添加学院
     * 
     * @param college 学院信息
     * @return 是否成功
     */
    Boolean addCollege(College college);

    /**
     * 返回学院总人数并持久化到数据库中
     * 
     * @param collegeId 学院ID
     * @return 总人数
     */
    Integer getAndSetcount(Integer collegeId);

    /**
     * 修改学院
     * 
     * @param college 学院信息
     * @return 是否成功
     */
    Boolean updateCollege(College college);

    /**
     * 删除学院
     * 
     * @param collegeId 学院ID
     * @return 是否成功
     */
    Boolean delCollege(Integer collegeId);

    /**
     * 获取学院信息
     * 
     * @param collegeId 学院ID
     * @return 学院信息
     */
    College getCollegeById(Integer collegeId);

    /**
     *   获取分页学院列表
     */
    List<College> getCollegePage(String name, int offset, int limit);

    int getCollegeCount(String name);

}