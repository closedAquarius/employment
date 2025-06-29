package com.gr.geias.service;

import com.gr.geias.dto.EmploymentInformationMsg;
import com.gr.geias.model.EmploymentInformation;
import com.gr.geias.model.PersonInfo;

import java.util.List;

/**
 * 就业信息服务接口
 */
public interface EmploymentInformationService {
    /**
     * 查找毕业生信息
     * @param information 查询信息
     * @param pagenum 页码
     * @param personInfo 用户权限
     * @param salary 工资区间
     * @return 就业信息消息对象
     */
    EmploymentInformationMsg getEmploymentInfoList(EmploymentInformation information, Integer pagenum, PersonInfo personInfo, Integer[] salary);

    /**
     * 根据给出的信息查询数量
     * @param information 信息
     * @param personInfo 用户权限
     * @param salary 工资区间
     * @return 符合条件的记录数
     */
    Integer getCount(EmploymentInformation information, PersonInfo personInfo, Integer[] salary);

    /**
     * 添加毕业生就业信息
     * @param employmentInformation 就业信息
     * @return 影响行数
     */
    Integer addEmpoymentInfo(EmploymentInformation employmentInformation);

    /**
     * 通过学号获取毕业生就业信息
     * @param studentNum 学号
     * @return 就业信息
     */
    EmploymentInformation getInfoByStudentNum(Integer studentNum);

    /**
     * 更新毕业生就业信息
     * @param employmentInformation 就业信息
     * @return 影响行数
     */
    Integer updateInfo(EmploymentInformation employmentInformation);
}