package com.gr.geias.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 就业信息Excel导出数据传输对象
 */
@Data
public class EmploymentInformationExcal implements Serializable {
    /**
     * 信息ID
     */
    @ExcelProperty("Id")
    private Integer informationId;
    
    /**
     * 学号
     */
    @ExcelProperty("学号")
    private Integer studentNum;
    
    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;
    
    /**
     * 性别
     */
    @ExcelProperty("性别")
    private String gender;
    
    /**
     * 学院名称
     */
    @ExcelProperty("学院")
    private String collegeName;
    
    /**
     * 专业名称
     */
    @ExcelProperty("专业")
    private String specialtyName;
    
    /**
     * 班级名称
     */
    @ExcelProperty("班级")
    private String className;
    
    /**
     * 就职地区
     */
    @ExcelProperty("就职地区")
    private String areaName;
    
    /**
     * 职业属性
     */
    @ExcelProperty("职业属性")
    private String unitName;
    
    /**
     * 就业途径
     */
    @ExcelProperty("就业途径")
    private String wayName;
    
    /**
     * 薪资
     */
    @ExcelProperty("工资")
    private String salary;
    
    /**
     * 创建时间
     */
    @ExcelProperty("录入时间")
    private Date createTime;
    
    /**
     * 备注信息
     */
    @ExcelProperty("信息")
    private String msg;
} 