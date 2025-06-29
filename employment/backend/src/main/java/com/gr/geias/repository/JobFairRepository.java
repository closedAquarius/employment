package com.gr.geias.repository;

import com.gr.geias.dto.JobFairAppliedDTO;
import com.gr.geias.dto.JobFairAvailableDTO;
import com.gr.geias.dto.JobFairWithCompaniesDTO;
import com.gr.geias.model.JobFair;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface JobFairRepository {
    List<JobFairWithCompaniesDTO> getAllJobFairsWithCompanies();

    @Insert("INSERT INTO tb_job_fair (title, description, location, start_time, end_time, create_time, organizer_id, booth_total) " +
            "VALUES (#{title}, #{description}, #{location}, #{startTime}, #{endTime}, #{createTime}, #{organizerId}, #{boothTotal})")
    void insertJobFair(JobFair jobFair);

    @Select("SELECT jf.* FROM tb_job_fair jf " +
            "WHERE (SELECT COUNT(*) FROM tb_job_fair_company jfc WHERE jfc.job_fair_id = jf.job_fair_id AND jfc.status = 1) < jf.booth_total")
    List<JobFair> selectJobFairsWithAvailableBooths();


    // 查询所有招聘会基本信息（含展位总数）
    List<JobFairAvailableDTO> selectAllJobFairsWithBooths();

    // 查询某个招聘会已被占用的展位编号（仅 status=1 表示通过）
    List<Integer> selectOccupiedBooths(@Param("jobFairId") Integer jobFairId);

    // 其他功能用到的补充接口
    int getBoothTotal(@Param("jobFairId") Integer jobFairId);

    int countApprovedCompanies(@Param("jobFairId") Integer jobFairId);

    List<JobFairAvailableDTO> selectUnappliedJobFairs(@Param("companyId") Integer companyId);

    List<JobFairAppliedDTO> selectAppliedJobFairs(@Param("companyId") Integer companyId);
}
