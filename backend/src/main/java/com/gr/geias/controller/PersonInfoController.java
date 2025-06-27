package com.gr.geias.controller;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.enums.EnableStatusEnums;
import com.gr.geias.service.PersonInfoService;
import com.gr.geias.service.SpecialtyService;
import com.gr.geias.util.JwtUtil;
import io.jsonwebtoken.Claims;
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
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam("username") String username,
                                     @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<>(3);
        if (username == null || username.equals("") || password == null || password.equals("")) {
            map.put("success", false);
            map.put("errMsg", "用户名或者密码为空");
        } else {
            PersonInfo login = personInfoService.login(username, password);
            if (login != null) {
                String token = JwtUtil.generateToken(login.getPersonId(),login.getUsername()); //生成 token
                map.put("success", true);
                map.put("token", token); //返回 token
            } else {
                map.put("success", false);
                map.put("errMsg", "用户名或者密码错误");
            }
        }
        return map;
    }

    /**
     * 注册
     *
     * @param username 用户名
     * @param password 密码
     * @param personName 姓名
     * @param enableStatus 角色状态
     * @return 注册结果
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        @RequestParam("personName") String personName,
                                        @RequestParam("enableStatus") Integer enableStatus) {
        Map<String, Object> map = new HashMap<>(2);
        try {
            PersonInfo personInfo = new PersonInfo();
            personInfo.setUsername(username);
            personInfo.setPassword(password);
            personInfo.setPersonName(personName);
            personInfo.setEnableStatus(enableStatus);
            Boolean success = personInfoService.registerPerson(personInfo);
            map.put("success", success);
            if (!success) {
                map.put("errMsg", "注册失败");
            }
        } catch (IllegalArgumentException e) {
            map.put("success", false);
            map.put("errMsg", e.getMessage());
        }
        return map;
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    @GetMapping("/getuser")
    public Map<String, Object> getUser(@RequestHeader("Authorization") String token) {
        Map<String, Object> map = new HashMap<>(2);
        try {
            Claims claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("userId");
            PersonInfo person = personInfoService.getPersonById(userId);
            map.put("success", true);
            map.put("person", person);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token无效或已过期");
        }
        return map;
    }


    /**
     * 更新用户信息
     * @param personname 用户姓名
     * @param username 用户名
     * @param password 密码
     * @return 更新结果
     */
    @PostMapping("/updateuser")
    public Map<String, Object> updateUser(@RequestHeader("Authorization") String token,
                                          @RequestParam("personname") String personname,
                                          @RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<>(2);
        try {
            Claims claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("userId");
            PersonInfo person = personInfoService.getPersonById(userId);
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
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token无效或已过期");
        }
        return map;
    }


    /**
     * 添加人脸识别信息
     *
     * @param file 人脸图像数据
     * @return 添加结果
     */
    @PostMapping("/addFace")
    public Map<String, Object> addFace(@RequestHeader("Authorization") String token,
                                       @RequestParam("file") String file) throws Exception {
        Map<String, Object> map = new HashMap<>(2);
        try {
            Claims claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("userId");
            PersonInfo person = personInfoService.getPersonById(userId);

            String[] split = file.split(",");
            Boolean added = personInfoService.addFace(person, split[1]);
            if (added) {
                // 不再用 session 维护用户信息
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "添加出错");
            }
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token无效或已过期");
        }
        return map;
    }


    /**
     * 人脸登录
     *
     * @param file 人脸图像数据
     * @return 登录结果
     */
    @PostMapping("/faceLogin")
    public Map<String, Object> faceLogin(@RequestParam("file") String file) {
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
            String token = JwtUtil.generateToken(personInfo.getPersonId(), personInfo.getUsername());
            map.put("success", true);
            map.put("token", token);
        } else {
            map.put("success", false);
            map.put("errMsg", "登录失败");
        }
        return map;
    }

} 