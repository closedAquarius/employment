package com.gr.geias.service.impl;

import com.gr.geias.dto.JobFairAppliedDTO;
import com.gr.geias.dto.JobFairAvailableDTO;
import com.gr.geias.dto.JobFairWithCompaniesDTO;
import com.gr.geias.model.JobFair;
import com.gr.geias.repository.JobFairRepository;
import com.gr.geias.service.JobFairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobFairServiceImpl implements JobFairService {
    @Autowired
    private JobFairRepository jobFairRepository;

    @Override
    public List<JobFairWithCompaniesDTO> getAllJobFairsWithCompanies() {
        return jobFairRepository.getAllJobFairsWithCompanies();
    }

    @Override
    public void insertJobFair(JobFair jobFair) {
        jobFairRepository.insertJobFair(jobFair);
    }

    @Override
    public List<JobFairAvailableDTO> getJobFairsWithBoothStatus() {
        List<JobFairAvailableDTO> fairs = jobFairRepository.selectAllJobFairsWithBooths();
        for (JobFairAvailableDTO fair : fairs) {
            List<Integer> occupied = jobFairRepository.selectOccupiedBooths(fair.getJobFairId());
            fair.setOccupiedBooths(occupied);
        }
        return fairs;
    }

    @Override
    public int getBoothTotal(Integer jobFairId) {
        return jobFairRepository.getBoothTotal(jobFairId);
    }

    @Override
    public int countApprovedCompanies(Integer jobFairId) {
        return jobFairRepository.countApprovedCompanies(jobFairId);
    }

    @Override
    public List<JobFairAvailableDTO> getUnappliedJobFairsWithBooths(Integer companyId) {
        List<JobFairAvailableDTO> fairs = jobFairRepository.selectUnappliedJobFairs(companyId);
        for (JobFairAvailableDTO fair : fairs) {
            List<Integer> occupied = jobFairRepository.selectOccupiedBooths(fair.getJobFairId()); // status = 0,1
            fair.setOccupiedBooths(occupied);
        }
        return fairs;
    }

    @Override
    public List<JobFairAppliedDTO> getAppliedJobFairs(Integer companyId) {
        List<JobFairAppliedDTO> fairs = jobFairRepository.selectAppliedJobFairs(companyId);
        return fairs;
    }
}
