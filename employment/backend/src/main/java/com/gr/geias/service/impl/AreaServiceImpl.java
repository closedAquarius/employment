package com.gr.geias.service.impl;

import com.gr.geias.model.Area;
import com.gr.geias.repository.AreaRepository;
import com.gr.geias.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地区信息服务实现类
 */
@Service
public class AreaServiceImpl implements AreaService {
    
    @Autowired
    private AreaRepository areaRepository;
    
    @Override
    public List<Area> getArea(Area area) {
        return areaRepository.queryArea(area);
    }
} 