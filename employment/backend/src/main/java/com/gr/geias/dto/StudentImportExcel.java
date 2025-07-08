package com.gr.geias.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentImportExcel {
    @ExcelProperty("姓名")
    private String personName;

    @ExcelProperty("学院名称")
    private String collegeName;
}

