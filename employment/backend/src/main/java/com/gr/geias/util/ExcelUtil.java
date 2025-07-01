package com.gr.geias.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.gr.geias.dto.EmploymentInformationExcal;
import com.gr.geias.model.EmploymentInformation;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.repository.EmploymentInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel导出工具类
 */
@Component
public class ExcelUtil {
    @Autowired
    private EmploymentInformationRepository employmentInformationRepository;

    private final static String TAG = "on";

    /**
     * 创建Excel并写入响应流
     *
     * @param response HTTP响应对象
     * @param personInfo 用户信息
     * @param excludeColumnFiledNames 需要排除的列名
     * @throws IOException IO异常
     */
    public void createExcel(HttpServletResponse response,
                            PersonInfo personInfo,
                            Set<String> excludeColumnFiledNames) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());

        String fileName = URLEncoder.encode("毕业生就业信息" + format, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriter excelWriter = EasyExcel.write(
                response.getOutputStream(),
                EmploymentInformationExcal.class)
                .excludeColumnFiledNames(excludeColumnFiledNames).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("就业信息").build();
        Integer integer = employmentInformationRepository.queryListCount(null, personInfo, null);
        Integer num = (integer / 100) + 1;
        for (int i = 0; i < num; i++) {
            excelWriter.write(getData(i, personInfo), writeSheet);
        }
        // 关闭流
        excelWriter.finish();
    }

    /**
     * 获取需要排除的列
     */
    public Set<String> getExcludeColumn(String id, String studentNum, String name, String gender, 
                                      String classGrade, String specialty, String college, 
                                      String area, String unit, String way, String salary) {
        Set<String> set = new HashSet<String>();
        if (id == null) {
            set.add("informationId");
        }
        if (studentNum == null) {
            set.add("studentNum");
        }
        if (name == null) {
            set.add("name");
        }
        if (gender == null) {
            set.add("gender");
        }
        if (classGrade == null) {
            set.add("className");
        }
        if (specialty == null) {
            set.add("specialtyName");
        }
        if (college == null) {
            set.add("collegeName");
        }
        if (area == null) {
            set.add("areaName");
        }
        if (unit == null) {
            set.add("unitName");
        }
        if (way == null) {
            set.add("wayName");
        }
        if (salary == null) {
            set.add("salary");
        }
        return set;
    }

    /**
     * 获取数据列表
     */
    private List<EmploymentInformationExcal> getData(Integer pageNum, PersonInfo personInfo) {
        List<EmploymentInformationExcal> list = new ArrayList<>();
        int indexNum = PageMath.pageNumtoRowIndex(pageNum + 1, 100);
        List<EmploymentInformation> employmentInformations =
                employmentInformationRepository.queryList(null, indexNum,
                        100, personInfo, null);
        for (EmploymentInformation info : employmentInformations) {
            EmploymentInformationExcal infoExcal = getInfoExcel(info);
            list.add(infoExcal);
        }
        return list;
    }

    /**
     * 转换实体为Excel导出对象
     */
    private EmploymentInformationExcal getInfoExcel(EmploymentInformation info) {
        EmploymentInformationExcal infoExcal = new EmploymentInformationExcal();
        infoExcal.setAreaName(info.getArea().getAreaName());
        infoExcal.setClassName(info.getClassGrade().getClassName());
        infoExcal.setCollegeName(info.getCollege().getCollegeName());
        infoExcal.setCreateTime(info.getCreateTime());
        infoExcal.setGender(getGender(info.getGender()));
        infoExcal.setInformationId(info.getInformationId());
        infoExcal.setMsg(info.getMsg());
        infoExcal.setName(info.getName());
        infoExcal.setSalary(info.getSalary());
        infoExcal.setSpecialtyName(info.getSpecialty().getSpecialtyName());
        infoExcal.setStudentNum(info.getStudentNum());
        infoExcal.setUnitName(info.getUnitKind().getUnitName());
        infoExcal.setWayName(info.getEmploymentWay().getVayName());
        return infoExcal;
    }

    /**
     * 获取性别字符串
     */
    private String getGender(Integer gender) {
        return gender == 1 ? "男" : "女";
    }
} 