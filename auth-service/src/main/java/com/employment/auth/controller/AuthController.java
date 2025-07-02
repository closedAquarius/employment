package com.employment.auth.controller;

import com.employment.auth.controller.dto.UserDTO;
import com.employment.auth.model.User;
import com.employment.auth.service.UserService;
import com.employment.auth.util.AuthConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Auth Service is running");
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginInfo) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        
        String token = userService.login(username, password);
        
        if (token != null) {
            User user = userService.getUserByToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("realName", user.getRealName());
            response.put("userType", user.getUserType());
            response.put("avatar", user.getAvatar());
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * 验证令牌
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // 去掉"Bearer "前缀
        
        if (userService.validateToken(token)) {
            User user = userService.getUserByToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("realName", user.getRealName());
            response.put("userType", user.getUserType());
            response.put("avatar", user.getAvatar());
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * 验证令牌 (与validate端点功能相同，但路径不同)
     */
    @GetMapping("/verifyToken")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // 去掉"Bearer "前缀
        
        if (userService.validateToken(token)) {
            User user = userService.getUserByToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("realName", user.getRealName());
            response.put("userType", user.getUserType());
            response.put("avatar", user.getAvatar());
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * 刷新令牌
     */
    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // 去掉"Bearer "前缀
        
        String newToken = userService.refreshToken(token);
        
        if (newToken != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // 去掉"Bearer "前缀
        
        userService.logout(token);
        
        return ResponseEntity.ok().build();
    }

    /**
     * 同步用户信息
     */
    @PostMapping("/sync-user")
    public ResponseEntity<?> syncUser(@RequestBody User user, @RequestParam String sourceSystem) {
        Long userId = userService.syncUser(user, sourceSystem);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "用户同步成功");
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDTO userDTO) {
        // 检查用户名是否已存在
        User existingUser = userService.getUserByUsername(userDTO.getUsername());
        if (existingUser != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        // DTO转换为实体
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 设置默认值
        user.setStatus(AuthConstant.UserStatus.ENABLED);
        // 默认注册为学生用户
        user.setUserType(AuthConstant.UserType.STUDENT);
        
        Long userId = userService.createUser(user);
        
        // 自动登录
        String token = userService.login(user.getUsername(), userDTO.getPassword());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "注册成功");
        response.put("userId", userId);
        response.put("token", token);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 