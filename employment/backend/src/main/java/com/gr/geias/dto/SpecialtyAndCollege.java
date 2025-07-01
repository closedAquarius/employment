package com.gr.geias.dto;

import com.gr.geias.model.College;
import com.gr.geias.model.Specialty;
import lombok.Data;

/**
 * 专业和学院关联数据传输对象
 */
@Data
public class SpecialtyAndCollege {
    /**
     * 专业信息
     */
    private Specialty specialty;
    
    /**
     * 学院信息
     */
    private College college;
    
    /**
     * 统计总数
     */
    private Integer sum;
} 