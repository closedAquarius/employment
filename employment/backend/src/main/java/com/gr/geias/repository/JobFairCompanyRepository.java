package com.gr.geias.repository;

import com.gr.geias.model.JobFairCompany;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    @Select({
            "<script>",
            "SELECT",
            "    jfc.job_fair_id,",
            "    jfc.company_id,",
            "    jfc.booth_location,",
            "    jfc.status,",
            "    jf.title AS jobFairTitle,",
            "    c.company_name AS companyName",
            "FROM tb_job_fair_company jfc",
            "LEFT JOIN tb_job_fair jf ON jfc.job_fair_id = jf.job_fair_id",
            "LEFT JOIN tb_company_info c ON jfc.company_id = c.company_id",
            "<where>",
            "    <if test='status != null'>",
            "        AND jfc.status = #{status}",
            "    </if>",
            "</where>",
            "ORDER BY jfc.job_fair_id DESC",
            "</script>"
    })
    List<JobFairCompany> selectJobFairCompaniesWithStatus(@Param("status") Integer status);
}
