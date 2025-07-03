package com.gr.geias.controller;

import com.gr.geias.model.OperationLog;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.Specialty;
import com.gr.geias.enums.EnableStatusEnums;
import com.gr.geias.service.OperationLogService;
import com.gr.geias.service.PersonInfoService;
import com.gr.geias.service.RouterService;
import com.gr.geias.service.SpecialtyService;
import com.gr.geias.util.JwtUtil;
import com.gr.geias.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        /*
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> routes = new ArrayList<>();

            // ===================  创建权限管理路由 ===================
            Map<String, Object> permissionRouter = new HashMap<>();
            permissionRouter.put("path", "/permission");

            // 权限管理meta
            Map<String, Object> permissionMeta = new HashMap<>();
            permissionMeta.put("title", "权限管理");
            permissionMeta.put("icon", "ep:lollipop");
            permissionMeta.put("showLink", false);
            permissionMeta.put("rank", 10);
            permissionRouter.put("meta", permissionMeta);

            // 权限管理子路由
            List<Map<String, Object>> permissionChildren = new ArrayList<>();

            // 页面权限
            Map<String, Object> pagePermission = new HashMap<>();
            pagePermission.put("path", "/permission/page/index");
            pagePermission.put("name", "PermissionPage");

            Map<String, Object> pageMeta = new HashMap<>();
            pageMeta.put("title", "页面权限");
            pageMeta.put("roles", Arrays.asList("admin", "common", "0", "1", "2"));
            pagePermission.put("meta", pageMeta);

            permissionChildren.add(pagePermission);

            // 按钮权限
            Map<String, Object> buttonPermission = new HashMap<>();
            buttonPermission.put("path", "/permission/button");

            Map<String, Object> buttonMeta = new HashMap<>();
            buttonMeta.put("title", "按钮权限");
            buttonMeta.put("roles", Arrays.asList("admin", "common", "0", "1", "2"));
            buttonPermission.put("meta", buttonMeta);

            // 按钮权限的子路由
            List<Map<String, Object>> buttonChildren = new ArrayList<>();

            // 路由返回按钮权限
            Map<String, Object> buttonRouter = new HashMap<>();
            buttonRouter.put("path", "/permission/button/router");
            buttonRouter.put("component", "permission/button/index");
            buttonRouter.put("name", "PermissionButtonRouter");

            Map<String, Object> buttonRouterMeta = new HashMap<>();
            buttonRouterMeta.put("title", "路由返回按钮权限");
            buttonRouterMeta.put("auths", Arrays.asList(
                    "permission:btn:add",
                    "permission:btn:edit",
                    "permission:btn:delete"
            ));
            buttonRouter.put("meta", buttonRouterMeta);

            buttonChildren.add(buttonRouter);

            // 登录接口返回按钮权限
            Map<String, Object> buttonLogin = new HashMap<>();
            buttonLogin.put("path", "/permission/button/login");
            buttonLogin.put("component", "permission/button/perms");
            buttonLogin.put("name", "PermissionButtonLogin");

            Map<String, Object> buttonLoginMeta = new HashMap<>();
            buttonLoginMeta.put("title", "登录接口返回按钮权限");
            buttonLogin.put("meta", buttonLoginMeta);

            buttonChildren.add(buttonLogin);

            buttonPermission.put("children", buttonChildren);
            permissionChildren.add(buttonPermission);

            permissionRouter.put("children", permissionChildren);

            // ===================  创建毕业生就业信息路由 ===================
            Map<String, Object> graduateRouter = new HashMap<>();
            graduateRouter.put("path", "/graduate");
            graduateRouter.put("name", "Graduate");
            graduateRouter.put("redirect", "/graduate");

            // 毕业生就业信息meta
            Map<String, Object> graduateMeta = new HashMap<>();
            graduateMeta.put("icon", "custom/graduate");
            graduateMeta.put("title", "毕业生就业信息");
            graduateMeta.put("rank", 0);
            graduateRouter.put("meta", graduateMeta);

            // 毕业生就业信息子路由
            List<Map<String, Object>> graduateChildren = new ArrayList<>();

            // 毕业生就业总体数据
            Map<String, Object> graduateData = new HashMap<>();
            graduateData.put("path", "/graduate/data");
            graduateData.put("name", "GraduateData");
            graduateData.put("component", "graduate/data");
            graduateData.put("roles", Arrays.asList("admin", "common"));

            Map<String, Object> graduateDataMeta = new HashMap<>();
            graduateDataMeta.put("title", "毕业生就业总体数据");
            graduateData.put("meta", graduateDataMeta);

            graduateChildren.add(graduateData);

            // 毕业生主要就业方式
            Map<String, Object> graduateType = new HashMap<>();
            graduateType.put("path", "/graduate/type");
            graduateType.put("name", "GraduateType");
            graduateType.put("component", "graduate/type");
            graduateType.put("roles", Arrays.asList("admin", "common"));

            Map<String, Object> graduateTypeMeta = new HashMap<>();
            graduateTypeMeta.put("title", "毕业生主要就业方式");
            graduateType.put("meta", graduateTypeMeta);

            graduateChildren.add(graduateType);

            // 毕业生就业工作性质
            Map<String, Object> graduateAttribute = new HashMap<>();
            graduateAttribute.put("path", "/graduate/attribute");
            graduateAttribute.put("name", "GraduateAttribut");
            graduateAttribute.put("component", "graduate/attribute");
            graduateAttribute.put("roles", Arrays.asList("admin", "common"));

            Map<String, Object> graduateAttributeMeta = new HashMap<>();
            graduateAttributeMeta.put("title", "毕业生就业工作性质");
            graduateAttribute.put("meta", graduateAttributeMeta);

            graduateChildren.add(graduateAttribute);

            // 毕业生主要就业位置
            Map<String, Object> graduateLocation = new HashMap<>();
            graduateLocation.put("path", "/graduate/location");
            graduateLocation.put("name", "GraduateLocation");
            graduateLocation.put("component", "graduate/location");
            graduateLocation.put("roles", Arrays.asList("admin", "common"));

            Map<String, Object> graduateLocationMeta = new HashMap<>();
            graduateLocationMeta.put("title", "毕业生主要就业位置");
            graduateLocation.put("meta", graduateLocationMeta);

            graduateChildren.add(graduateLocation);

            graduateRouter.put("children", graduateChildren);

            // ===================  组装最终结果 ===================
            routes.add(permissionRouter);
            routes.add(graduateRouter);

            result.put("success", true);
            result.put("data", routes);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("errMsg", "获取路由配置失败: " + e.getMessage());
        }

        return result;
        */
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

} 