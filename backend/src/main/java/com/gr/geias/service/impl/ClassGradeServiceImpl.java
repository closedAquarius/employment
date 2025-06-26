package com.gr.geias.service.impl;

import com.gr.geias.model.ClassGrade;
import com.gr.geias.model.EmploymentInformation;
import com.gr.geias.model.OrganizationNum;
import com.gr.geias.repository.ClassGradeRepository;
import com.gr.geias.repository.EmploymentInformationRepository;
import com.gr.geias.repository.OrganizationNumRepository;
import com.gr.geias.service.ClassGradeService;
import com.gr.geias.service.CollegeService;
import com.gr.geias.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 班级信息服务实现类
 */
@Service
public class ClassGradeServiceImpl implements ClassGradeService {
    
    @Autowired
    private ClassGradeRepository classGradeRepository;
    
    @Autowired
    private OrganizationNumRepository organizationNumRepository;
    
    @Autowired
    private EmploymentInformationRepository employmentInformationRepository;
    
    @Autowired
    private SpecialtyService specialtyService;
    
    @Autowired
    private CollegeService collegeService;

    @Override
    public List<ClassGrade> getClassGrade(Integer specialtyId, Integer adminId) {
        return classGradeRepository.queryClassGrade(specialtyId, adminId);
    }

    @Override
    @Transactional
    public Boolean addClassGrade(ClassGrade classGrade, Integer sum, Integer collegeId) {
        Integer result = classGradeRepository.insertClassGrede(classGrade);
        if (result > 0) {
            try {
                OrganizationNum organizationNum = new OrganizationNum();
                organizationNum.setClassId(classGrade.getClassId());
                organizationNum.setSum(sum);
                Integer insertResult = organizationNumRepository.insertOrganizationNum(organizationNum);
                collegeService.getAndSetcount(collegeId);
                specialtyService.getAndSetSpecialtyCount(classGrade.getSpecialtyId());
                if (insertResult > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException("添加出错", e);
            }
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean updateClassGrade(ClassGrade classGrade, Integer sum, Integer collegeId) {
        try {
            Integer updateResult = classGradeRepository.updateClassGrede(classGrade);
            Integer numUpdateResult = organizationNumRepository.updateNumByClassGradeId(sum, classGrade.getClassId());
            collegeService.getAndSetcount(collegeId);
            specialtyService.getAndSetSpecialtyCount(classGrade.getSpecialtyId());
            
            if (updateResult > 0 && numUpdateResult > 0) {
                return true;
            } else {
                throw new RuntimeException("更新错误");
            }
        } catch (Exception e) {
            throw new RuntimeException("更新错误", e);
        }
    }

    @Override
    @Transactional
    public Boolean delClassGrade(Integer classId) {
        try {
            Integer delNumResult = organizationNumRepository.delClassGrade(classId);
            
            EmploymentInformation employmentInformation = new EmploymentInformation();
            ClassGrade classGrade = new ClassGrade();
            classGrade.setClassId(classId);
            employmentInformation.setClassGrade(classGrade);
            
            employmentInformationRepository.delEmploymentInformation(employmentInformation);
            Integer delClassResult = classGradeRepository.delClassGrede(classId);
            
            if (delClassResult > 0 && delNumResult > 0) {
                return true;
            } else {
                throw new RuntimeException("删除记录失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除记录失败", e);
        }
    }
} 