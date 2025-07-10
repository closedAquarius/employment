package com.gr.geias.repository;

import com.gr.geias.model.News;
import org.apache.ibatis.annotations.Param;
import com.gr.geias.model.JobPosting;
import java.util.List;

public interface JobPostingRepository {
    //插入信息
    int insert(JobPosting job);
    int batchInsert(@Param("list") List<JobPosting> list);

    //清楚所有数据
    int deleteAll();

    // 如需按 ID 查询
    List<JobPosting> findById(@Param("jobId") int jobId,
                              @Param("offset") int offset,
                              @Param("size") int size
    );

    //查找所有的Job信息
    List<JobPosting> selectJobList( @Param("offset") int offset,
                                    @Param("size") int size
    );
    //获取count
    int selectTotalJobCount();
    //获取搜索的数目
    int findByIdCount(int jobId);

}
