package com.gr.geias.service.impl;

import com.gr.geias.model.College;
import com.gr.geias.model.OrganizationNum;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.repository.CollegeRepository;
import com.gr.geias.repository.OrganizationNumRepository;
import com.gr.geias.repository.PersonInfoRepository;
import com.gr.geias.service.CollegeService;
import com.gr.geias.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 学院服务实现类
 */
@Service
public class CollegeServiceImpl implements CollegeService {
    @Autowired
    CollegeRepository collegeRepository;
    @Autowired
    PersonInfoRepository personInfoRepository;
    @Autowired
    OrganizationNumRepository organizationNumRepository;
    @Autowired
    SpecialtyService specialtyService;

    @Override
    public List<College> getCollege(Integer adminId) {
        return collegeRepository.queryCollege(adminId);
    }

    @Override
    @Transactional
    public Boolean addCollege(College college) {
        int i = collegeRepository.addCollege(college);
        if (i > 0) {
            PersonInfo personInfo = new PersonInfo();
            personInfo.setPersonId(college.getAdminId());
            personInfo.setCollegeId(college.getCollegeId());
            Integer integer = personInfoRepository.updatePersonofCollege(personInfo);
            OrganizationNum organizationNum = new OrganizationNum();
            organizationNum.setCollegeId(college.getCollegeId());
            organizationNum.setSum(0);
            Integer integer1 = organizationNumRepository.insertOrganizationNum(organizationNum);
            if (integer > 0 && integer1 > 0) {
                return true;
            } else {
                throw new RuntimeException("添加过程中出错");
            }
        } else {
            throw new RuntimeException("添加过程中出错");
        }
    }

    @Override
    public Integer getAndSetcount(Integer collegeId) {
        Integer integer = collegeRepository.quereyCollegeCount(collegeId);
        if (integer == null) {
            integer = 0;
        }
        organizationNumRepository.updataOrganizationNumByCollegeId(integer, collegeId);
        return integer;
    }

    @Override
    @Transactional
    public Boolean updateCollege(College college) {
        if (college.getAdminId() != null) {
            try {
                College college1 = collegeRepository.queryCollegeById(college.getCollegeId());
                PersonInfo personInfo = new PersonInfo();
                personInfo.setPersonId(college1.getAdminId());
                personInfo.setCollegeId(null);
                personInfoRepository.updatePersonofCollege(personInfo);
                collegeRepository.updateCollege(college);
                PersonInfo personInfo1 = new PersonInfo();
                personInfo1.setPersonId(college.getAdminId());
                personInfo1.setCollegeId(college.getCollegeId());
                personInfoRepository.updatePersonofCollege(personInfo1);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("修改时出错");
            }
        } else {
            collegeRepository.updateCollege(college);
            return true;
        }
    }

    @Override
    @Transactional
    public Boolean delCollege(Integer collegeId) {
        try {
            College college = collegeRepository.queryCollegeById(collegeId);
            organizationNumRepository.delCollegeCount(collegeId);
            PersonInfo personInfo = new PersonInfo();
            personInfo.setCollegeId(null);
            personInfo.setPersonId(college.getAdminId());
            personInfoRepository.updatePersonofCollege(personInfo);
            List<Specialty> specialty = specialtyService.getSpecialty(collegeId);
            for (int i = 0; i < specialty.size(); i++) {
                specialtyService.delSpecialty(specialty.get(i).getSpecialtyId());
            }
            personInfoRepository.delPerson(collegeId);
            collegeRepository.delCollegeById(collegeId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("删除出错,请重试");
        }
    }

    @Override
    public College getCollegeById(Integer collegeId) {
        return collegeRepository.queryCollegeById(collegeId);
    }

    @Override
    public List<College> getCollegePage(String name, int offset, int limit) {
        return collegeRepository.queryCollegePage(name, offset, limit);
    }

    @Override
    public int getCollegeCount(String name) {
        return collegeRepository.queryCollegeCount(name);
    }

}