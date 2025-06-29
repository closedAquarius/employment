package com.gr.geias.repository;

import com.gr.geias.model.EmploymentWay;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EmploymentWayRepository {
    /**
     * 获取工作来源
     * @return
     */
    @Select("select * from tb_employment_way")
    List<EmploymentWay> queryEmploymentWay();

}