package com.gr.geias.repository;

import com.gr.geias.model.Area;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AreaRepository {
    /**
     * 获取地区信息
     * @param area
     * @return
     */
    List<Area> queryArea(@Param("area") Area area);
}