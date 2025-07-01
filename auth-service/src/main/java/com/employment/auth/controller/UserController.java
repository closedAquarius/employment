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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody UserDTO userDTO,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7); // 去掉"Bearer "前缀
        User currentUser = userService.getUserByToken(token);
        
        // 检查权限，只有管理员可以创建用户
        if (currentUser == null || currentUser.getUserType() != AuthConstant.UserType.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // DTO转换为实体
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        Long userId = userService.createUser(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("message", "用户创建成功");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 获取用户列表
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(required = false) Integer userType,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User currentUser = userService.getUserByToken(token);
        
        // 检查权限，只有管理员和教师可以查看用户列表
        if (currentUser == null || 
            (currentUser.getUserType() != AuthConstant.UserType.ADMIN && 
             currentUser.getUserType() != AuthConstant.UserType.TEACHER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<User> users;
        if (userType != null) {
            users = userService.getUsersByType(userType);
        } else {
            // 获取所有用户的逻辑，这里简化处理
            users = new ArrayList<>();
            for (int i = 0; i <= 3; i++) {
                users.addAll(userService.getUsersByType(i));
            }
        }
        
        // 实体转换为DTO
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            // 不返回密码
            dto.setPassword(null);
            userDTOs.add(dto);
        }
        
        return ResponseEntity.ok(userDTOs);
    }
    
    /**
     * 获取单个用户
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User currentUser = userService.getUserByToken(token);
        
        // 检查权限，管理员可以查看任何用户，其他用户只能查看自己
        if (currentUser == null || 
            (currentUser.getUserType() != AuthConstant.UserType.ADMIN && 
             !currentUser.getId().equals(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        // 不返回密码
        userDTO.setPassword(null);
        
        return ResponseEntity.ok(userDTO);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDTO userDTO,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User currentUser = userService.getUserByToken(token);
        
        // 检查权限，管理员可以更新任何用户，其他用户只能更新自己
        if (currentUser == null || 
            (currentUser.getUserType() != AuthConstant.UserType.ADMIN && 
             !currentUser.getId().equals(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // 非管理员不能修改自己的权限
        if (currentUser.getUserType() != AuthConstant.UserType.ADMIN && 
            userDTO.getUserType() != null && 
            !userDTO.getUserType().equals(currentUser.getUserType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // DTO转换为实体
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setId(userId);
        
        boolean success = userService.updateUser(user);
        
        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "用户更新成功");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * 修改用户状态（启用/禁用）
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User currentUser = userService.getUserByToken(token);
        
        // 检查权限，只有管理员可以修改用户状态
        if (currentUser == null || currentUser.getUserType() != AuthConstant.UserType.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        boolean success = userService.updateUserStatus(userId, status);
        
        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "用户状态更新成功");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * 修改用户类型（权限）
     */
    @PutMapping("/{userId}/type")
    public ResponseEntity<Map<String, Object>> updateUserType(
            @PathVariable Long userId,
            @RequestParam Integer userType,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User currentUser = userService.getUserByToken(token);
        
        // 检查权限，只有管理员可以修改用户类型
        if (currentUser == null || currentUser.getUserType() != AuthConstant.UserType.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // 验证用户类型是否有效
        if (userType < 0 || userType > 3) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "无效的用户类型");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean success = userService.updateUserType(userId, userType);
        
        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "用户权限更新成功");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * 检查用户权限
     */
    @GetMapping("/{userId}/permission")
    public ResponseEntity<Map<String, Object>> checkPermission(
            @PathVariable Long userId,
            @RequestParam Integer requiredType,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User currentUser = userService.getUserByToken(token);
        
        // 检查权限，只有管理员和教师可以检查用户权限
        if (currentUser == null || 
            (currentUser.getUserType() != AuthConstant.UserType.ADMIN && 
             currentUser.getUserType() != AuthConstant.UserType.TEACHER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        boolean hasPermission = userService.checkUserPermission(userId, requiredType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("hasPermission", hasPermission);
        
        return ResponseEntity.ok(response);
    }
} 