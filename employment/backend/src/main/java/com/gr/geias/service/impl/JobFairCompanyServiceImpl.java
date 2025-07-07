package com.gr.geias.service.impl;

import com.gr.geias.model.JobFairCompany;
import com.gr.geias.repository.JobFairCompanyRepository;
import com.gr.geias.service.JobFairCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobFairCompanyServiceImpl implements JobFairCompanyService {
    @Autowired
    private JobFairCompanyRepository jobFairCompanyRepository;

    @Override
    public void applyToJobFair(JobFairCompany jfc) {
        jobFairCompanyRepository.applyToJobFair(jfc);
    }

    @Override
    public boolean existsApplication(Integer jobFairId, Integer companyId) {
        return jobFairCompanyRepository.existsApplication(jobFairId, companyId);
    }

    @Override
    public void reviewApplication(Integer jobFairId, Integer companyId, Integer status) {
        jobFairCompanyRepository.reviewApplication(jobFairId, companyId, status);
    }

    @Override
    public int countApprovedCompanies(Integer jobFairId) {
        return jobFairCompanyRepository.countApprovedCompanies(jobFairId);
    }

    @Override
    public List<JobFairCompany> selectJobFairCompaniesWithStatus(Integer status) {
        return jobFairCompanyRepository.selectJobFairCompaniesWithStatus(status);
    }
}
