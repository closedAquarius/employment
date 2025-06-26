package com.gr.geias.controller;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.enums.EnableStatusEnums;
import com.gr.geias.service.PersonInfoService;
import com.gr.geias.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员信息控制器
 */
@RestController
@RequestMapping("/api/personinfo")
public class PersonInfoController {
    @Autowired
    private PersonInfoService personInfoService;
    
    @Autowired
    private SpecialtyService specialtyService;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(3);
        if (username == null || username.equals("") || password == null || password.equals("")) {
            map.put("success", false);
            map.put("errMsg", "用户名或者密码为空");
        } else {
            PersonInfo login = personInfoService.login(username, password);
            if (login != null) {
                request.getSession().setAttribute("person", login);
                if (login.getEnableStatus() == EnableStatusEnums.PREXY.getState()) {
                    List<Specialty> specialtyList = specialtyService.getSpecialty(login.getCollegeId());
                    List<PersonInfo> person0 = personInfoService.getPersonByCollegeId(login.getCollegeId());
                    request.getSession().setAttribute("person0List", person0);
                    request.getSession().setAttribute("specialtyList", specialtyList);
                }
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "用户名或者密码错误");
            }
        }
        return map;
    }

    /**
     * 获取当前用户信息
     *
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/getuser")
    public Map<String, Object> getUser(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(2);
        PersonInfo person = (PersonInfo) request.getSession().getAttribute("person");
        map.put("success", true);
        map.put("person", person);
        return map;
    }

    /**
     * 更新用户信息
     *
     * @param request HTTP请求
     * @param personname 用户姓名
     * @param username 用户名
     * @param password 密码
     * @return 更新结果
     */
    @PostMapping("/updateuser")
    public Map<String, Object> updateUser(HttpServletRequest request,
                                         @RequestParam("personname") String personname,
                                         @RequestParam("username") String username,
                                         @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<>(2);
        PersonInfo person = (PersonInfo) request.getSession().getAttribute("person");
        person.setUsername(username);
        person.setPassword(password);
        person.setPersonName(personname);
        Boolean updated = personInfoService.updatePerson(person);
        if (updated) {
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("errMsg", "修改出错");
        }
        return map;
    }

    /**
     * 添加人脸识别信息
     *
     * @param file 人脸图像数据
     * @param request HTTP请求
     * @return 添加结果
     */
    @PostMapping("/addFace")
    public Map<String, Object> addFace(@RequestParam("file") String file,
                                      HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>(2);
        PersonInfo person = (PersonInfo) request.getSession().getAttribute("person");
        String[] split = file.split(",");
        Boolean added = personInfoService.addFace(person, split[1]);
        if (added) {
            request.getSession().setAttribute("person", person);
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("errMsg", "添加出错");
        }
        return map;
    }

    /**
     * 人脸登录
     *
     * @param file 人脸图像数据
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/faceLogin")
    public Map<String, Object> faceLogin(@RequestParam("file") String file,
                                       HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(2);
        String[] split = file.split(",");
        PersonInfo personInfo = personInfoService.checkFace(split[1]);
        if (personInfo == null) {
            map.put("success", false);
            map.put("errMsg", "没有识别到人脸");
        } else if (personInfo.getPersonId() == null) {
            map.put("success", false);
            map.put("errMsg", "没有该用户");
        } else if (personInfo.getPersonId() != null) {
            request.getSession().setAttribute("person", personInfo);
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("errMsg", "登录失败");
        }
        return map;
    }
} 