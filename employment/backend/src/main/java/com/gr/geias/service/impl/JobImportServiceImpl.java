package com.gr.geias.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gr.geias.model.JobPosting;
import com.gr.geias.repository.JobPostingRepository;
import com.gr.geias.service.JobImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class JobImportServiceImpl implements JobImportService {

    @Autowired
    JobPostingRepository mapper;

    @Autowired
    ObjectMapper objectMapper;


    public List<JobPosting> getJobByJobId(int offset, int size, Integer jobId) {
        return mapper.findById(offset, size,jobId);
    }

    @Override
    public List<JobPosting> getAllJobs(int offset, int size) {
        return mapper.selectJobList(offset, size);
    }

    @Override
    public int getAllJobsCount() {
        return mapper.selectTotalJobCount();
    }

    @Transactional
    public void importFromJson(String classpathLocation) throws IOException {
        mapper.deleteAll();
        // 1) 读取 JSON 文件
        ClassPathResource resource = new ClassPathResource("data/all_data.json");
        System.out.println("=============================");
        System.out.println("读入的resource文件为"+resource.getFilename());
        try (InputStream is = resource.getInputStream()) {
            // 2) 解析为 List<JobPosting>
            List<JobPosting> list = objectMapper.readValue(
                    is,
                    new TypeReference<List<JobPosting>>() {
                    }
            );
            System.out.println("==========================");
            System.out.println("list的长度为"+list.size());
            if (list != null && !list.isEmpty()) {
                // 3) 批量插入
                mapper.batchInsert(list);
            }
        }
        catch (Exception e) {
            System.out.println("错误为"+e.getMessage());
            e.printStackTrace();
        }
    }

    public int findByIdCount(Integer jobId) {
        return mapper.findByIdCount(jobId);
    }
}
