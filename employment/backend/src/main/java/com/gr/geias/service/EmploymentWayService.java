package com.gr.geias.service;

import com.gr.geias.model.EmploymentWay;

import java.util.List;

/**
 * 就业方式服务接口
 */
public interface EmploymentWayService {
    /**
     * 获取所有就业方式
     * @return 就业方式列表
     */
    List<EmploymentWay> getEmploymentWay();
}