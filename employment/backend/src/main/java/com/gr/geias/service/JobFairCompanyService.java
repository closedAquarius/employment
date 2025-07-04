package com.gr.geias.service;

import com.gr.geias.dto.JobFairAppliedDTO;
import com.gr.geias.model.JobFairCompany;

import java.util.List;

public interface JobFairCompanyService {
    void applyToJobFair(JobFairCompany jfc);

    boolean existsApplication(Integer jobFairId, Integer companyId);

    void reviewApplication(Integer jobFairId, Integer companyId, Integer status);

    int countApprovedCompanies(Integer jobFairId);

    List<JobFairCompany> selectJobFairCompaniesWithStatus(Integer status);
}
