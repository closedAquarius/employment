package com.gr.geias.service;

import com.gr.geias.model.Specialty;

import java.util.List;

/**
 * 专业信息服务接口
 */
public interface SpecialtyService {

    /**
     * 获取学院下的所有专业
     * 
     * @param collegeId 学院ID
     * @return 专业列表
     */
    List<Specialty> getSpecialty(Integer collegeId);
    
    /**
     * 添加专业
     * 
     * @param specialty 专业信息
     * @return 是否成功
     */
    Boolean addSpecialty(Specialty specialty);

    /**
     * 更新专业信息
     * 
     * @param specialty 专业信息
     * @return 是否成功
     */
    Boolean updateSpecialty(Specialty specialty);

    /**
     * 删除专业信息
     * 
     * @param specialtyId 专业ID
     * @return 是否成功
     */
    Boolean delSpecialty(Integer specialtyId);

    /**
     * 通过专业id获取专业信息
     * 
     * @param specialtyId 专业ID
     * @return 专业信息
     */
    Specialty getSpecialtyById(Integer specialtyId);

    /**
     * 获取并持久化专业人数
     * 
     * @param specialtyId 专业ID
     * @return 人数
     */
    Integer getAndSetSpecialtyCount(Integer specialtyId);

    /**
     * 分页获取专业列表
     * @param collegeId
     * @param name
     * @param offset
     * @param limit
     * @return
     */
    List<Specialty> getSpecialtyPage(Integer collegeId, String name, int offset, int limit);

    /**
     *  获取符合条件的专业总数
     * @param collegeId
     * @param name
     * @return
     */
    int getSpecialtyCount(Integer collegeId, String name);

} 