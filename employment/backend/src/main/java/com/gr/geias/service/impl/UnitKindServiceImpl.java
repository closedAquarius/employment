package com.gr.geias.service.impl;

import com.gr.geias.model.UnitKind;
import com.gr.geias.repository.UnitKindRepository;
import com.gr.geias.service.UnitKindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作类型服务实现类
 */
@Service
public class UnitKindServiceImpl implements UnitKindService {
    @Autowired
    UnitKindRepository unitKindRepository;

    @Override
    public List<UnitKind> getUnitKind() {
        return unitKindRepository.queryUnitKind();
    }
}