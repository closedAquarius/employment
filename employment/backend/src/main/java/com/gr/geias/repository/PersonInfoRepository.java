package com.gr.geias.repository;

import com.gr.geias.model.PersonInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 人员信息Repository接口
 */
public interface PersonInfoRepository {
    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @Select("select * from tb_person_info where username=#{username} and password=#{password} ")
    PersonInfo queryPerson(@Param("username") String username, @Param("password") String password);

    /**
     * 检查用户名是否存在
     * @param username
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_person_info WHERE username = #{username}")
    boolean checkUsernameExists(@Param("username") String username);

    /**
     * 根据id查询人员
     *
     * @param personId 用户ID
     * @return 用户信息
     */
    @Select("select * from tb_person_info where person_id=#{personId} ")
    PersonInfo queryPersonById(@Param("personId") Integer personId);

    /**
     * 查询学院管理
     *
     * @return 学院管理员列表
     */
    @Select("select person_id,person_name from tb_person_info where enable_Status=4 and college_id is NULL")
    List<PersonInfo> queryCollegePerson();

    /**
     * 更新用户所属学院
     *
     * @param personInfo 用户信息
     * @return 影响行数
     */
    Integer updatePersonofCollege(@Param("personInfo") PersonInfo personInfo);

    /**
     * 查询一个学院里所有的老师
     *
     * @param collegeId 学院ID
     * @return 教师列表
     */
    @Select("select * from tb_person_info where enable_Status=1 and college_id=#{collegeId}")
    List<PersonInfo> queryPersonByCollegeId(@Param("collegeId") Integer collegeId);

    /**
     * 删除一个学院的全部老师
     *
     * @param collegeId 学院ID
     * @return 影响行数
     */
    @Delete("delete from tb_person_info where college_id=#{collegeId}")
    Integer delPerson(@Param("collegeId") Integer collegeId);

    /**
     * 添加人员
     *
     * @param personInfo 人员信息
     * @return 影响行数
     */
    @Insert("insert into tb_person_info(enable_Status,person_name,create_time,password,username,college_id) " +
            "values(#{person.enableStatus},#{person.personName},#{person.createTime},#{person.password}," +
            "#{person.username},#{person.collegeId})")
    Integer insertPerson(@Param("person") PersonInfo personInfo);

    /**
     * 更新人员信息
     *
     * @param personInfo 人员信息
     * @return 影响行数
     */
    Integer updatePerson(@Param("person") PersonInfo personInfo);

    /**
     * 删除用户
     * 
     * @param personId 用户ID
     * @return 影响行数
     */
    @Delete("delete from tb_person_info where person_id=#{personId}")
    Integer delPersonById(@Param("personId") Integer personId);

    /**
     * 获取权限为4的用户
     * 
     * @return 管理员列表
     */
    @Select("select * from tb_person_info where enable_Status=2")
    List<PersonInfo> queryPerson1();

    /**
     * 更新用户人脸标识
     * 
     * @param personId 用户ID
     * @param faceToken 人脸标识
     * @return 影响行数
     */
    @Update("update tb_person_info set face_token=#{faceToken} where person_id=#{personId}")
    Integer updatePersonById(@Param("personId") Integer personId, @Param("faceToken") String faceToken);

    List<PersonInfo> queryPersonByCollegeIdPage(Integer collegeId, int offset, int limit);

    Integer queryPersonByCollegeIdCount(Integer collegeId);

    List<PersonInfo> queryPerson1Page(int offset, int limit);

    Integer queryPerson1Count();

    List<PersonInfo> selectAllTeachers(@Param("offset") int offset, @Param("pageSize") int pageSize);

    int countAllTeachers();

} 