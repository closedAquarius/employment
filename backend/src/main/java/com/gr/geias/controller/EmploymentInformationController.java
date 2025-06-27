package com.gr.geias.controller;


import com.gr.geias.dto.AreaCount;
import com.gr.geias.dto.EmploymentInformationMsg;
import com.gr.geias.model.*;
import com.gr.geias.enums.EnableStatusEnums;
import com.gr.geias.service.*;
import com.gr.geias.util.ExcelUtil;
import com.gr.geias.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @description 前端控制器
 */
@RestController
@RequestMapping("/employmentinformation")
public class EmploymentInformationController {
    @Autowired
    EmploymentInformationService informationService;
    @Autowired
    PersonInfoService personInfoService;
    @Autowired
    AreaService areaService;
    @Autowired
    EmploymentWayService employmentWayService;
    @Autowired
    UnitKindService unitKindService;
    @Autowired
    ExcelUtil excelUtil;

    /**
     *  获取毕业生就业信息列表 权限 0，1，2
     * @param pageNum
     * @param areaId
     * @param employmentWayId
     * @param unitId
     * @param levelId
     * @param name
     * @param salary
     * @return
     */
    @RequestMapping(value = "/getemploymentinfo", method = RequestMethod.GET)
    public Object findAll(@RequestHeader("Authorization") String token,
                          @RequestParam("pageNum") Integer pageNum,
                          @RequestParam(value = "areaId", required = false) Integer areaId,
                          @RequestParam(value = "employmentWayId", required = false) Integer employmentWayId,
                          @RequestParam(value = "unitId", required = false) Integer unitId,
                          @RequestParam(value = "levelId", required = false) Integer levelId,
                          @RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "salary", required = false) String salary) {
        Map<String,Object> map = new HashMap<String,Object>(3);
        try {
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("userId");
        PersonInfo person = personInfoService.getPersonById(userId);
        EmploymentInformation employmentInformation = new EmploymentInformation();
        if (areaId != null) {
            Area area = new Area();
            area.setAreaId(areaId);
            employmentInformation.setArea(area);
        }
        if (employmentWayId != null) {
            EmploymentWay employmentWay = new EmploymentWay();
            employmentWay.setEmploymentWayId(employmentWayId);
            employmentInformation.setEmploymentWay(employmentWay);
        }
        if (unitId != null) {
            UnitKind unitKind = new UnitKind();
            unitKind.setUnitId(unitId);
            employmentInformation.setUnitKind(unitKind);
        }
        if (levelId != null) {
            if (person.getEnableStatus() == EnableStatusEnums.TEACHER.getState()) {
                ClassGrade classGrade = new ClassGrade();
                classGrade.setClassId(levelId);
                employmentInformation.setClassGrade(classGrade);
            }
            if (person.getEnableStatus() == EnableStatusEnums.PREXY.getState()) {
                Specialty specialty = new Specialty();
                specialty.setSpecialtyId(levelId);
                employmentInformation.setSpecialty(specialty);
            }
            if (person.getEnableStatus() == EnableStatusEnums.ADMINISTRATOR.getState()) {
                College college = new College();
                college.setCollegeId(levelId);
                employmentInformation.setCollege(college);
            }
        }
        if (name != null) {
            employmentInformation.setName(name);
        }
        Integer[] a = null;
        if (salary != null) {
            String[] split = salary.split("｜");
            a = new Integer[2];
            a[0] = Integer.parseInt(split[0]);
            a[1] = Integer.parseInt(split[1]);
        }
        EmploymentInformationMsg employmentInfoList =
                informationService.getEmploymentInfoList(employmentInformation, pageNum, person, a);
        if (employmentInfoList.getSuccess()){
            map.put("success", true);
            map.put("list", employmentInfoList.getList());
            map.put("count", employmentInfoList.getCount());
        }else {
            map.put("success", false);
            map.put("errMsg", "出现错误");
        }
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token无效或已过期");
        }
        return map;
    }

    /**
     * 获取 地区数量 列表 权限 0，1，2
     * @return
     */
    @RequestMapping(value = "/getcountbyarea", method = RequestMethod.GET)
    public Map<String,Object> getCountByArea(@RequestHeader("Authorization") String token) {
        Map<String,Object> result = new HashMap<>(2);
        try {
            Claims claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("userId");
            PersonInfo person = personInfoService.getPersonById(userId);

            List<Area> areaList = areaService.getArea(null);
            List<AreaCount> list = new ArrayList<>(36);
            EmploymentInformation employmentInformation = new EmploymentInformation();

            for (Area area : areaList) {
                employmentInformation.setArea(area);
                Integer count = informationService.getCount(employmentInformation, person, null);
                AreaCount areaCount = new AreaCount();
                areaCount.setName(area.getAreaName());
                areaCount.setValue(count);
                list.add(areaCount);
            }
            result.put("success", true);
            result.put("map", list);
        } catch (Exception e) {
            result.put("success", false);
            result.put("errMsg", "Token无效或已过期");
        }
        return result;
    }


    /**
     * 获取就业途径数量 列表 权限 0，1，2
     * @return
     */
    @RequestMapping(value = "/getcountbyemploymentway",method = RequestMethod.GET)
    public Map<String,Object> getCountByEmploymentWay(@RequestHeader("Authorization") String token) {
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("userId");
        PersonInfo person = personInfoService.getPersonById(userId);
        List<EmploymentWay> areaList = employmentWayService.getEmploymentWay();
        Map<String,Object> ruslt = new HashMap<String,Object>(2);
        List<AreaCount> list = new ArrayList<AreaCount>(6);
        try{
            EmploymentInformation employmentInformation = new EmploymentInformation();
            for (int i = 0; i < areaList.size(); i++) {
                EmploymentWay employmentWay = areaList.get(i);
                employmentInformation.setEmploymentWay(employmentWay);
                Integer count = informationService.getCount(employmentInformation, person,null);
                AreaCount areaCount = new AreaCount();
                areaCount.setName(employmentWay.getVayName());
                areaCount.setValue(count);
                list.add(areaCount);
            }
            ruslt.put("success", true);
            ruslt.put("map", list);
            return ruslt;
        }catch (Exception e){
            ruslt.put("success", false);
            ruslt.put("errMsg", e.getMessage());
            return ruslt;
        }

    }

    /**
     * 获取 职业分类 数量 列表 0，1，2
     * @return
     */
    @RequestMapping(value = "/getcountbyunitkind",method = RequestMethod.GET)
    public Map<String,Object> getCountByUnitKind(@RequestHeader("Authorization") String token){
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("userId");
        PersonInfo person = personInfoService.getPersonById(userId);
        List<UnitKind> areaList = unitKindService.getUnitKind();
        Map<String,Object> ruslt = new HashMap<String,Object>(2);
        List<AreaCount> list = new ArrayList<AreaCount>(6);
        List<String> stringList = new ArrayList<String>(6);
        try{
            EmploymentInformation employmentInformation = new EmploymentInformation();
            for (int i = 0; i < areaList.size(); i++) {
                UnitKind unitKind = areaList.get(i);
                stringList.add(unitKind.getUnitName());
                employmentInformation.setUnitKind(unitKind);
                Integer count = informationService.getCount(employmentInformation, person,null);
                AreaCount areaCount = new AreaCount();
                areaCount.setName(unitKind.getUnitName());
                areaCount.setValue(count);
                list.add(areaCount);
            }
            ruslt.put("success", true);
            ruslt.put("map", list);
            ruslt.put("nameList", stringList);
            return ruslt;
        }catch (Exception e){
            ruslt.put("success", false);
            ruslt.put("errMsg", e.getMessage());
            return ruslt;
        }


    }

    /**
     * 导出数据
     * @param response
     */
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public void download(HttpServletResponse response,
                         @RequestHeader("Authorization") String authorization,
                         @RequestParam(value = "id",required = false)String id,
                         @RequestParam(value = "studentNum",required = false)String studentNum,
                         @RequestParam(value = "name",required = false)String name,
                         @RequestParam(value = "gender",required = false)String gender,
                         @RequestParam(value = "class",required = false)String classGrade,
                         @RequestParam(value = "specialty",required = false)String specialty,
                         @RequestParam(value = "college",required = false)String college,
                         @RequestParam(value = "area",required = false)String area,
                         @RequestParam(value = "unit",required = false)String unit,
                         @RequestParam(value = "way",required = false)String way,
                         @RequestParam(value = "salary",required = false)String salary){
        try {
            String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
            Claims claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("userId");
            PersonInfo personInfo = personInfoService.getPersonById(userId);

            Set<String> excludeColumn = excelUtil.getExcludeColumn(id, studentNum, name, gender, classGrade, specialty, college, area, unit, way, salary);
            excelUtil.createExcel(response, personInfo, excludeColumn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 