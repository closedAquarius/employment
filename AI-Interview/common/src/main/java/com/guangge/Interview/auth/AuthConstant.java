package com.guangge.Interview.auth;

/**
 * 认证相关常量
 */
public class AuthConstant {

    /**
     * 认证请求头名称
     */
    public static final String HEADER_AUTH = "Authorization";
    
    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * Cookie名称
     */
    public static final String COOKIE_NAME = "auth_token";
    
    /**
     * 用户状态
     */
    public static class UserStatus {
        /**
         * 禁用
         */
        public static final int DISABLED = 0;
        
        /**
         * 启用
         */
        public static final int ENABLED = 1;
    }
    
    /**
     * 用户类型
     */
    public static class UserType {
        /**
         * 学生
         */
        public static final int STUDENT = 0;
        
        /**
         * 教师
         */
        public static final int TEACHER = 1;
        
        /**
         * 管理员
         */
        public static final int ADMIN = 2;
        
        /**
         * 企业HR
         */
        public static final int HR = 3;
        
        /**
         * 面试者
         */
        public static final int INTERVIEWEE = 4;
    }
    
    /**
     * 来源系统
     */
    public static class SourceSystem {
        /**
         * 认证服务
         */
        public static final String AUTH_SERVICE = "auth-service";
        
        /**
         * AI面试系统
         */
        public static final String AI_INTERVIEW = "ai-interview";
        
        /**
         * 就业信息系统
         */
        public static final String EMPLOYMENT = "employment";
    }
}
