package com.gr.geias.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gr.geias.model.EmploymentWay;
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

    @Transactional
    public void importFromJson(String classpathLocation) throws IOException {
        // 1) 读取 JSON 文件
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        try (InputStream is = resource.getInputStream()) {
            // 2) 解析为 List<JobPosting>
            List<JobPosting> list = objectMapper.readValue(
                    is,
                    new TypeReference<List<JobPosting>>() {
                    }
            );
            if (list != null && !list.isEmpty()) {
                // 3) 批量插入
                mapper.batchInsert(list);
            }
        }
    }
}
