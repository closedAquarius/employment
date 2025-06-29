package com.gr.geias.service;

import com.gr.geias.model.CompanyInfo;

public interface CompanyInfoService {
    boolean addCompany(CompanyInfo companyInfo);
    boolean updateCompany(CompanyInfo companyInfo);
    boolean confirmCompany(int companyId);
    CompanyInfo getCompanyByPersonId(int personId);
}
