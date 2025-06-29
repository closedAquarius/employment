package com.employment.auth.service;

import com.employment.auth.model.User;

public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回token，失败返回null
     */
    String login(String username, String password);
    
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
} 