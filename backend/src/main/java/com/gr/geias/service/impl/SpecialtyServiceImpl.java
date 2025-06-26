package com.gr.geias.service.impl;

import com.gr.geias.model.Specialty;
import com.gr.geias.repository.SpecialtyRepository;
import com.gr.geias.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
} 