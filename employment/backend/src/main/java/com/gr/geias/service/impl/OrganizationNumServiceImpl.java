package com.gr.geias.service.impl;

import com.gr.geias.model.OrganizationNum;
import com.gr.geias.repository.OrganizationNumRepository;
import com.gr.geias.service.OrganizationNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 组织人数服务实现类
 */
@Service
public class OrganizationNumServiceImpl implements OrganizationNumService {
    @Autowired
    OrganizationNumRepository organizationNumRepository;

    @Override
    public Integer getcollegeCount(Integer collegeId) {
        return organizationNumRepository.queryCountByCollegeId(collegeId);
    }

    @Override
    public Integer getspecialtyCount(Integer specialtyId) {
        return organizationNumRepository.queryCountByspecialtyId(specialtyId);
    }

    @Override
    public Integer getClassGradeCount(Integer classId) {
        return organizationNumRepository.queryCountByclassId(classId);
    }

    @Override
    public Boolean updateClassGradesum(OrganizationNum organizationNum) {
        Integer integer = organizationNumRepository.updateNumByClassGradeId(organizationNum.getSum(), organizationNum.getClassId());
        if (integer > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Integer geiAllCollegeSum() {
        return organizationNumRepository.queryAllCollegeSum();
    }
}