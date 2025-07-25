package com.gr.geias.controller;

import com.alibaba.excel.EasyExcel;
import com.gr.geias.dto.StudentExportExcel;
import com.gr.geias.dto.StudentImportExcel;
import com.gr.geias.dto.StudentWithCollege;
import com.gr.geias.model.OperationLog;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.enums.EnableStatusEnums;
import com.gr.geias.service.*;
import com.gr.geias.util.ExcelUtil;
import com.gr.geias.util.JwtUtil;
import com.gr.geias.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;


/**
 * 人员信息控制器
 */
@RestController
@RequestMapping("/api/personinfo")
public class PersonInfoController {
    @Autowired
    private PersonInfoService personInfoService;
    
    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private RouterService routeService;

    @Autowired
    private CollegeService collegeService;


    @Autowired
    private ExcelUtil excelUtil;

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
                String accessToken = JwtUtil.generateAccessToken(login.getPersonId(), login.getUsername(),login.getEnableStatus());
                String refreshToken = JwtUtil.generateRefreshToken(login.getPersonId(), login.getUsername(),login.getEnableStatus());
                Date expires = new Date(System.currentTimeMillis() + 1000 * 60 * 30); // 30分钟
                Map<String, Object> data = new HashMap<>();
                data.put("id", login.getPersonId());
                data.put("avatar", ""); // 可根据你的字段改
                data.put("username", login.getUsername());
                data.put("nickname", login.getPersonName());
                ArrayList<Integer> roleList = new ArrayList<Integer>();
                roleList.add(login.getEnableStatus());
                data.put("roles", roleList);
                data.put("permissions", ""); // 你暂时没实现权限控制
                data.put("accessToken", accessToken);
                data.put("refreshToken", refreshToken);
                data.put("expires", expires);

                //登录操作的日志
                OperationLog log = new OperationLog();
                log.setPersonId(login.getPersonId());
                log.setUsername(login.getUsername());
                log.setEnableStatus(login.getEnableStatus());
                log.setOperationType("POST /api/personinfo/login");
                log.setTarget("用户登录");
                log.setSuccess(true);
                log.setOperationTime(LocalDateTime.now());
                operationLogService.saveLog(log);

                map.put("success", true);
                map.put("data", data); //返回 token
            } else {
                map.put("success", false);
                map.put("errMsg", "用户名或者密码错误");
            }
        }
        return map;
    }

    @PostMapping("/get-async-routes")
    public Map<String, Object> getAsyncRoutes(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> result = new HashMap<>();

        try {
            String role = Integer.toString((Integer) requestBody.get("role"));

            List<Map<String, Object>> routes = routeService.getRoutesByRole(role);
            result.put("success", true);
            result.put("data", routes);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("errMsg", "获取路由配置失败: " + e.getMessage());
        }

        return result;
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
     * 刷新token
     * @param refreshToken
     * @return
     */
    @PostMapping("/refresh-token")
    public Map<String, Object> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        Map<String, Object> map = new HashMap<>(2);
        try {
            Claims claims = JwtUtil.parseRefreshToken(refreshToken);
            Integer userId = (Integer) claims.get("userId");
            String username = claims.getSubject();
            Integer roles = (Integer) claims.get("roles");

            String newAccessToken = JwtUtil.generateAccessToken(userId, username,roles);
            String newRefreshToken = JwtUtil.generateRefreshToken(userId, username,roles);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", newAccessToken);
            data.put("refreshToken", newRefreshToken);
            data.put("expires", new Date(System.currentTimeMillis() + 1000 * 60 * 30)); // 30分钟后

            map.put("success", true);
            map.put("data", data);
        } catch (ExpiredJwtException e) {
            map.put("success", false);
            map.put("errMsg", "RefreshToken 已过期");
        } catch (JwtException e) {
            map.put("success", false);
            map.put("errMsg", "RefreshToken 无效");
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
            Claims claims = TokenUtil.extractClaims(token);
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
            Claims claims = TokenUtil.extractClaims(token);

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
            Claims claims = TokenUtil.extractClaims(token);

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
            String token = JwtUtil.generateAccessToken(personInfo.getPersonId(), personInfo.getUsername(),personInfo.getEnableStatus());
            map.put("success", true);
            map.put("token", token);
        } else {
            map.put("success", false);
            map.put("errMsg", "登录失败");
        }
        return map;
    }

    @PostMapping("/import-students")
    public Map<String, Object> importStudents(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 读取Excel文件，转成导入DTO列表
            List<StudentImportExcel> importList = EasyExcel.read(file.getInputStream())
                    .head(StudentImportExcel.class)
                    .sheet()
                    .doReadSync();

            if (importList == null || importList.isEmpty()) {
                result.put("success", false);
                result.put("errMsg", "导入文件为空");
                return result;
            }

            // 2. 调用Service批量转换并保存
            boolean success = personInfoService.importStudents(importList);

            if (success) {
                result.put("success", true);
                result.put("message", "批量导入成功");
            } else {
                result.put("success", false);
                result.put("errMsg", "批量导入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("errMsg", "导入异常: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/export-students")
    public void exportStudents(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("学生信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + fileName + ".xlsx\"; filename*=UTF-8''" + fileName + ".xlsx");


            List<StudentExportExcel> exportList = personInfoService.getAllStudents();
            EasyExcel.write(response.getOutputStream(), StudentExportExcel.class)
                    .sheet("学生信息")
                    .doWrite(exportList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/getAllStudents")
    public List<StudentWithCollege> getAllStudents() {
        return personInfoService.getAllStudentsWithCollege();
    }
} 