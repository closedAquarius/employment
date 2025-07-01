package com.gr.geias.service;

import com.gr.geias.model.PersonInfo;

import java.util.List;

/**
 * 人员信息服务接口
 */
public interface PersonInfoService {
    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    PersonInfo login(String username, String password);

    /**
     * 注册新用户
     * @param personInfo 用户信息
     * @return 是否成功
     */
    Boolean registerPerson(PersonInfo personInfo);

    /**
     * 根据id查询用户
     * @param personId 用户ID
     * @return 用户信息
     */
    PersonInfo getPersonById(Integer personId);

    /**
     * 获取所有学院管理员
     * @return 学院管理员列表
     */
    List<PersonInfo> getCollegePerson();

    /**
     * 获取一个学院里的所有教师
     * @param collegeId 学院ID
     * @return 教师列表
     */
    List<PersonInfo> getPersonByCollegeId(Integer collegeId);

    /**
     * 添加人员
     * @param personInfo 人员信息
     * @return 是否成功
     */
    Boolean insertPerson(PersonInfo personInfo);

    /**
     * 修改人员信息
     * @param personInfo 人员信息
     * @return 是否成功
     */
    Boolean updatePerson(PersonInfo personInfo);

    /**
     * 删除人员
     * @param personId 人员ID
     * @return 是否成功
     */
    Boolean delPerson(Integer personId);

    /**
     * 获取所有管理员信息
     * @return 管理员列表
     */
    List<PersonInfo> getPerson1();

    /**
     * 添加人脸识别信息
     * @param personInfo 人员信息
     * @param faceImage 人脸图像数据
     * @return 是否成功
     */
    Boolean addFace(PersonInfo personInfo, String faceImage);

    /**
     * 人脸识别登录
     * @param image 人脸图像数据
     * @return 用户信息
     */
    PersonInfo checkFace(String image);

    // 分页获取指定学院的辅导员（权限0）
    List<PersonInfo> getPersonByCollegeIdPage(Integer collegeId, int offset, int limit);

    // 获取指定学院辅导员总数
    int getPersonByCollegeIdCount(Integer collegeId);

    // 分页获取学院管理（权限1）
    List<PersonInfo> getPerson1Page(int offset, int limit);

    // 获取学院管理总数
    int getPerson1Count();

} 