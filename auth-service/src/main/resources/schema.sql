-- MySQL表结构（用于employment系统）
CREATE TABLE IF NOT EXISTS `employment_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `user_type` tinyint(1) DEFAULT NULL COMMENT '用户类型：1-学生，2-企业HR，3-就业指导老师，4-系统管理员',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `source_system` varchar(20) DEFAULT NULL COMMENT '用户来源系统',
  `source_id` bigint(20) DEFAULT NULL COMMENT '源系统用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一认证用户表';

-- PostgreSQL表结构（用于AI-Interview系统）
-- 注意：需要在PostgreSQL数据库中执行
/*
CREATE TABLE IF NOT EXISTS interview_user (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  phone VARCHAR(20),
  real_name VARCHAR(50),
  status SMALLINT DEFAULT 1,
  avatar VARCHAR(255),
  user_type SMALLINT,
  create_time TIMESTAMP,
  update_time TIMESTAMP,
  source_system VARCHAR(20),
  source_id BIGINT,
  CONSTRAINT uk_username UNIQUE (username)
);
*/ 