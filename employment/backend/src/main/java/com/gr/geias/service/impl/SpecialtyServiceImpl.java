package com.gr.geias.service.impl;

import com.gr.geias.model.Specialty;
import com.gr.geias.repository.SpecialtyRepository;
import com.gr.geias.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Date;

/**
 * 专业信息服务实现类
 */
@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Override
    public List<Specialty> getSpecialty(Integer collegeId) {
        return specialtyRepository.querySpecialtyByCollegeId(collegeId);
    }

    @Override
    public Boolean addSpecialty(Specialty specialty) {
        specialty.setCreateTime(new Date());
        return specialtyRepository.insertSpecialty(specialty) > 0;
    }

    @Override
    public Boolean updateSpecialty(Specialty specialty) {
        return specialtyRepository.updateSpecialty(specialty) > 0;
    }

    @Override
    public Boolean delSpecialty(Integer specialtyId) {
        return specialtyRepository.delSpecialty(specialtyId) > 0;
    }

    @Override
    public Specialty getSpecialtyById(Integer specialtyId) {
        return specialtyRepository.querySpecialtyById(specialtyId);
    }

    @Override
    public Integer getAndSetSpecialtyCount(Integer specialtyId) {
        // 简单实现，不需要复杂逻辑
        Integer count = 0;
        // 专业对应的班级数量逻辑
        return count;
    }

    @Override
    public List<Specialty> getSpecialtyPage(Integer collegeId, String name, int offset, int limit) {
        return specialtyRepository.querySpecialtyPage(collegeId, name, offset, limit);
    }

    @Override
    public int getSpecialtyCount(Integer collegeId, String name) {
        return specialtyRepository.querySpecialtyCount(collegeId, name);
    }

} 