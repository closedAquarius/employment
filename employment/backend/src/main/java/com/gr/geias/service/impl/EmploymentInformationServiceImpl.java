package com.gr.geias.service.impl;

import com.gr.geias.dto.EmploymentInformationMsg;
import com.gr.geias.model.EmploymentInformation;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.repository.EmploymentInformationRepository;
import com.gr.geias.service.EmploymentInformationService;
import com.gr.geias.util.PageMath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 就业信息服务实现类
 */
@Service
public class EmploymentInformationServiceImpl implements EmploymentInformationService {

    @Autowired
    EmploymentInformationRepository informationRepository;

    @Override
    public EmploymentInformationMsg getEmploymentInfoList(EmploymentInformation information, Integer pagenum, PersonInfo personInfo, Integer[] salary) {
        int i = PageMath.pageNumtoRowIndex(pagenum, 8);
        List<EmploymentInformation> employmentInformations = informationRepository.queryList(information, i, 8, personInfo, salary);
        Integer integer = informationRepository.queryListCount(information, personInfo, salary);
        EmploymentInformationMsg employmentInformationMsg = new EmploymentInformationMsg();
        if (integer >= 0) {
            employmentInformationMsg.setSuccess(true);
            employmentInformationMsg.setCount(integer);
            employmentInformationMsg.setList(employmentInformations);
        } else {
            employmentInformationMsg.setSuccess(false);
        }
        return employmentInformationMsg;
    }

    @Override
    public Integer getCount(EmploymentInformation information, PersonInfo personInfo, Integer[] salary) {
        return informationRepository.queryListCount(information, personInfo, salary);
    }

    @Override
    public Integer getCountByArea(EmploymentInformation information, PersonInfo personInfo, Integer[] salary) {
        return informationRepository.queryListCountByArea(information, personInfo, salary);
    }

    @Override
    public Integer addEmpoymentInfo(EmploymentInformation employmentInformation) {
        return informationRepository.insertEmploymentInformation(employmentInformation);
    }

    @Override
    public EmploymentInformation getInfoByStudentNum(Integer studentNum) {
        return informationRepository.qureyInfoByStudentNum(studentNum);
    }

    @Override
    public Integer updateInfo(EmploymentInformation employmentInformation) {
        return informationRepository.updayeInfo(employmentInformation);
    }
}