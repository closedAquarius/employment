package com.gr.geias.repository;

import com.gr.geias.model.JobFairCompany;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface JobFairCompanyRepository {
    @Insert("INSERT INTO tb_job_fair_company (job_fair_id, company_id, booth_location, status) " +
            "VALUES (#{jobFairId}, #{companyId}, #{boothLocation}, #{status})")
    void applyToJobFair(JobFairCompany jfc);

    @Select("SELECT COUNT(*) FROM tb_job_fair_company WHERE job_fair_id = #{jobFairId} AND company_id = #{companyId}")
    boolean existsApplication(@Param("jobFairId") Integer jobFairId, @Param("companyId") Integer companyId);

    @Update("UPDATE tb_job_fair_company SET status = #{status} " +
            "WHERE job_fair_id = #{jobFairId} AND company_id = #{companyId}")
    void reviewApplication(@Param("jobFairId") Integer jobFairId,
                           @Param("companyId") Integer companyId,
                           @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM tb_job_fair_company WHERE job_fair_id = #{jobFairId} AND status = 1")
    int countApprovedCompanies(@Param("jobFairId") Integer jobFairId);
}
