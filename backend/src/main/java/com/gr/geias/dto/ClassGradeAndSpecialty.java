package com.gr.geias.dto;

import com.gr.geias.model.ClassGrade;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import lombok.Data;

/**
 * 班级和专业关联数据传输对象
 */
@Data
public class ClassGradeAndSpecialty {
    /**
     * 班级信息
     */
    private ClassGrade classGrade;
    
    /**
     * 专业信息
     */
    private Specialty specialty;
    
    /**
     * 人员信息
     */
    private PersonInfo personInfo;
    
    /**
     * 统计总数
     */
    private Integer sum;
} 