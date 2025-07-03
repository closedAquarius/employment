package com.employment.auth.controller;

import com.employment.auth.controller.dto.UserDTO;
import com.employment.auth.model.User;
import com.employment.auth.service.UserService;
import com.employment.auth.util.AuthConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用于登录请求的DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, Object> result = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 验证Token有效性
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                System.out.println("验证失败: 未提供认证令牌");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "未提供认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(errorResponse);
            }
            
            System.out.println("验证令牌: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            boolean isValid = userService.validateToken(token);
            if (isValid) {
                User user = userService.getUserByToken(token);
                if (user == null) {
                    System.out.println("验证失败: 找不到对应的用户");
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "找不到对应的用户");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(errorResponse);
                }
                
                Map<String, Object> response = new HashMap<>();
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("realName", user.getRealName());
                response.put("userType", user.getUserType());
                response.put("avatar", user.getAvatar());
                
                System.out.println("验证成功: 用户 " + user.getUsername());
                return ResponseEntity.ok(response);
            } else {
                System.out.println("验证失败: 令牌无效");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "令牌无效或已过期");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(errorResponse);
            }
        } catch (Exception e) {
            System.out.println("验证过程发生异常: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "验证过程发生异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    
    /**
     * 验证令牌 (与validate端点功能相同，但路径不同)
     */
    @GetMapping("/verifyToken")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        String token;
        // 判断是否包含Bearer前缀
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 去掉"Bearer "前缀
        } else {
            // 没有Bearer前缀，直接使用
            token = authHeader;
        }
        
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
        Map<String, Object> loginResult = userService.login(user.getUsername(), userDTO.getPassword());
        String token = (String) loginResult.get("token");
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "注册成功");
        response.put("userId", userId);
        response.put("token", token);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 从请求中提取Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else if (bearerToken != null) {
            // 兼容不带Bearer前缀的情况
            return bearerToken;
        }
        return null;
    }
} 