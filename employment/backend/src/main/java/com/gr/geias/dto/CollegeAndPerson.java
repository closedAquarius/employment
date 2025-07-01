package com.gr.geias.dto;

import com.gr.geias.model.College;
import com.gr.geias.model.PersonInfo;
import lombok.Data;

/**
 * 学院和人员关联数据传输对象
 */
@Data
public class CollegeAndPerson {
    /**
     * 学院信息
     */
    private College college;
    
    /**
     * 人员信息
     */
    private PersonInfo personInfo;
    
    /**
     * 统计总数
     */
    private Integer sum;
} 