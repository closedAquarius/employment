package com.employment.auth.util;

/**
 * 认证相关常量
 */
public class AuthConstant {
    
    /**
     * 用户类型
     */
    public static final class UserType {
        public static final int STUDENT = 0;      // 学生
        public static final int TEACHER = 1;      // 教师
        public static final int ADMIN = 2;        // 管理员
        public static final int COMPANY_HR = 3;   // 企业HR
    }
    
    /**
     * 用户状态
     */
    public static final class UserStatus {
        public static final int DISABLED = 0;     // 禁用
        public static final int ENABLED = 1;      // 启用
    }
    
    /**
     * 系统来源
     */
    public static final class SourceSystem {
        public static final String EMPLOYMENT = "employment";  // 就业系统
        public static final String AI_INTERVIEW = "ai-interview";  // AI面试系统
    }
} 