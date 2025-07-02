package com.employment.auth.service;

import com.employment.auth.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回包含token和用户信息的Map，失败抛出异常
     */
    Map<String, Object> login(String username, String password);
    
    /**
     * 根据token获取用户信息
     * @param token JWT令牌
     * @return 用户信息
     */
    User getUserByToken(String token);
    
    /**
     * 验证token是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 刷新token
     * @param token 原token
     * @return 新token
     */
    String refreshToken(String token);
    
    /**
     * 登出
     * @param token JWT令牌
     */
    void logout(String token);
    
    /**
     * 同步用户信息（从源系统同步到认证系统）
     * @param user 用户信息
     * @param sourceSystem 源系统标识
     * @return 同步后的用户ID
     */
    Long syncUser(User user, String sourceSystem);
    
    /**
     * 创建新用户
     * @param user 用户信息
     * @return 新用户ID
     */
    Long createUser(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否更新成功
     */
    boolean updateUser(User user);
    
    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param status 新状态（0-禁用，1-启用）
     * @return 是否修改成功
     */
    boolean updateUserStatus(Long userId, Integer status);
    
    /**
     * 修改用户类型（权限）
     * @param userId 用户ID
     * @param userType 新用户类型（0-学生，1-教师，2-管理员，3-企业HR）
     * @return 是否修改成功
     */
    boolean updateUserType(Long userId, Integer userType);
    
    /**
     * 根据用户类型查询用户列表
     * @param userType 用户类型
     * @return 用户列表
     */
    List<User> getUsersByType(Integer userType);
    
    /**
     * 检查用户是否有指定权限
     * @param userId 用户ID
     * @param requiredUserType 需要的用户类型
     * @return 是否有权限
     */
    boolean checkUserPermission(Long userId, Integer requiredUserType);
} 