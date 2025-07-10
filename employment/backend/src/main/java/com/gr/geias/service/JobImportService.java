package com.gr.geias.service;

import com.gr.geias.dto.NewsCommentDTO;
import com.gr.geias.model.JobPosting;
import java.io.IOException;
import java.util.List;

public interface JobImportService {

    void importFromJson(String classpathLocation) throws IOException;
    List<JobPosting> getJobByJobId(int offset, int size,Integer jobId);
    List<JobPosting> getAllJobs(int offset, int size);
    int getAllJobsCount();
    int findByIdCount(Integer jobId);

}
