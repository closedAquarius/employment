package com.gr.geias.service;

import com.gr.geias.dto.JobFairAppliedDTO;
import com.gr.geias.dto.JobFairAvailableDTO;
import com.gr.geias.dto.JobFairWithCompaniesDTO;
import com.gr.geias.model.JobFair;

import java.util.List;

public interface JobFairService {
    List<JobFairWithCompaniesDTO> getAllJobFairsWithCompanies();

    void insertJobFair(JobFair jobFair);

    List<JobFairAvailableDTO> getJobFairsWithBoothStatus();

    int getBoothTotal(Integer jobFairId);

    int countApprovedCompanies(Integer jobFairId);

    List<JobFairAvailableDTO> getUnappliedJobFairsWithBooths(Integer companyId);

    List<JobFairAppliedDTO> getAppliedJobFairs(Integer companyId);
}
