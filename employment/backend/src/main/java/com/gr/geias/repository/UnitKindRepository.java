package com.gr.geias.repository;

import com.gr.geias.model.UnitKind;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UnitKindRepository {
    /**
     * 查询工作分类
     * @return
     */
    @Select("select * from tb_unit_kind")
    List<UnitKind> queryUnitKind();

}