package com.gr.geias.service.impl;

import com.gr.geias.model.EmploymentWay;
import com.gr.geias.repository.EmploymentWayRepository;
import com.gr.geias.service.EmploymentWayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 就业方式服务实现类
 */
@Service
public class EmploymentWayServiceImpl implements EmploymentWayService {
    @Autowired
    EmploymentWayRepository employmentWayRepository;

    @Override
    public List<EmploymentWay> getEmploymentWay() {
        return employmentWayRepository.queryEmploymentWay();
    }
}