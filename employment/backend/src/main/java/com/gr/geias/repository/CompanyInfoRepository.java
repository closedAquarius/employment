package com.gr.geias.repository;

import com.gr.geias.model.CompanyInfo;

public interface CompanyInfoRepository {

        int insertCompany(CompanyInfo companyInfo);
        int updateCompany(CompanyInfo companyInfo);
        int confirmCompany(int companyId);
        CompanyInfo getCompanyByPersonId(int personId);
}
