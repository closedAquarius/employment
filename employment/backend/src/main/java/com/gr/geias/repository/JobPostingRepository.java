package com.gr.geias.repository;

import org.apache.ibatis.annotations.Param;
import com.gr.geias.model.JobPosting;
import java.util.List;
import java.util.Map;

public interface JobPostingRepository {
    int insert(JobPosting job);
    int batchInsert(@Param("list") List<JobPosting> list);
    // 如需按 ID 查询、删除、更新，也可继续填写
    JobPosting findById(@Param("id") Integer id,@Param("jobId") Integer jobId);

    List<Map<String, Object>> getTopPreferredMajors();
}
