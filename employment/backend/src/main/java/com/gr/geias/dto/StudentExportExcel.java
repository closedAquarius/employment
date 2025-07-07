package com.gr.geias.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentExportExcel {
    @ExcelProperty("姓名")
    private String personName;

    @ExcelProperty("学院名称")
    private String collegeName;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("密码")
    private String password;
}
