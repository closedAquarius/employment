package com.gr.geias.service;

import com.gr.geias.model.UnitKind;

import java.util.List;

/**
 * 工作类型服务接口
 */
public interface UnitKindService {
    /**
     * 获取工作分类
     * @return 工作类型列表
     */
    List<UnitKind> getUnitKind();
}