package com.gr.geias.service;

import com.gr.geias.model.Area;

import java.util.List;

/**
 * 地区信息服务接口
 */
public interface AreaService {
    /**
     * 查询地区
     * 
     * @param area 查询条件
     * @return 地区列表
     */
    List<Area> getArea(Area area);
}