package com.gr.geias.service.impl;

import com.gr.geias.model.CompanyInfo;
import com.gr.geias.repository.CompanyInfoRepository;
import com.gr.geias.service.CompanyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyInfoServiceImpl implements CompanyInfoService {

    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Override
    public boolean addCompany(CompanyInfo companyInfo) {
        return companyInfoRepository.insertCompany(companyInfo)>0;
    }

    @Override
    public boolean updateCompany(CompanyInfo companyInfo) {
        return companyInfoRepository.updateCompany(companyInfo)>0;
    }

    @Override
    public boolean confirmCompany(int companyId) {
        return companyInfoRepository.confirmCompany(companyId)>0;
    }

    @Override
    public CompanyInfo getCompanyByPersonId(int personId) {
        return companyInfoRepository.getCompanyByPersonId(personId);
    }
}
