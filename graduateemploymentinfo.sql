/*
 Navicat Premium Dump SQL

 Source Server         : lqy0584
 Source Server Type    : MySQL
 Source Server Version : 90200 (9.2.0)
 Source Host           : localhost:3306
 Source Schema         : graduateemploymentinfo

 Target Server Type    : MySQL
 Target Server Version : 90200 (9.2.0)
 File Encoding         : 65001

 Date: 02/07/2025 15:23:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for employment_user
-- ----------------------------
DROP TABLE IF EXISTS `employment_user`;
CREATE TABLE `employment_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `user_type` tinyint(1) DEFAULT NULL COMMENT '用户类型：0-学生，1-教师，2-管理员，3-企业HR',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `source_system` varchar(20) DEFAULT NULL COMMENT '用户来源系统',
  `source_id` bigint DEFAULT NULL COMMENT '源系统用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一认证用户表';

-- ----------------------------
-- Records of employment_user
-- ----------------------------
BEGIN;
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (1, 'testuser', 'cc03e747a6afbbcbf8be7668acfebee5', 'test@example.com', NULL, '测试用户', 1, NULL, 0, '2025-06-30 10:36:12', '2025-06-30 10:36:12', 'auth-service', NULL);
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (2, 'newuser', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, 'New User', 1, NULL, 0, '2025-06-30 11:34:23', '2025-06-30 11:34:23', 'auth-service', NULL);
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (3, 'testuser2', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '测试用户2', 1, NULL, 0, '2025-06-30 13:36:55', '2025-06-30 13:36:55', 'auth-service', NULL);
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (4, 'empuser2', '123456', NULL, NULL, 'TestUser', 1, NULL, 0, '2025-06-30 13:36:56', '2025-06-30 13:36:56', 'employment', 34);
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (5, 'admin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', 'admin@example.com', '13800138000', '管理员', 1, NULL, 2, '2025-07-02 13:36:06', '2025-07-02 13:36:19', 'auth-service', NULL);
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (6, 'teacher', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', 'teacher@example.com', '13800138001', '教师用户', 1, NULL, 1, '2025-07-02 13:36:06', '2025-07-02 13:36:19', 'auth-service', NULL);
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (7, 'student', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', 'student@example.com', '13800138002', '学生用户', 1, NULL, 0, '2025-07-02 13:36:06', '2025-07-02 13:36:19', 'auth-service', NULL);
INSERT INTO `employment_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `status`, `avatar`, `user_type`, `create_time`, `update_time`, `source_system`, `source_id`) VALUES (8, 'hr', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', 'hr@example.com', '13800138003', '企业HR', 1, NULL, 3, '2025-07-02 13:36:06', '2025-07-02 13:36:19', 'auth-service', NULL);
COMMIT;

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `person_id` int NOT NULL,
  `enable_status` int DEFAULT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `operation_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `target` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `operation_time` datetime DEFAULT NULL,
  `success` tinyint(1) DEFAULT NULL,
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_operation_time` (`operation_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
BEGIN;
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (1, 32, 2, 'HR', 'GET /companyinfo/company-info', '未知操作：/companyinfo/company-info', 'Params: {}, Result: {success=true, company=CompanyInfo(companyId=1, companyName=1, companyIntro=1, address=1, areaId=null, createTime=Sun Jun 29 13:28:31 CST 2025, UnitId=1, confirmed=true, areaName=null)}', '0:0:0:0:0:0:0:1', '2025-06-29 14:23:18', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (2, 32, 2, 'HR', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-06-29 14:27:31', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (3, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:02:34', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (4, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:06:23', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (5, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:06:42', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (6, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:10:06', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (7, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:10:27', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (8, 3, 3, 'admin123456', 'POST /message/uploadFile', '上传文件消息', 'Params: {userId=[Ljava.lang.String;@5ef262e2, receiverId=[Ljava.lang.String;@28c0ede3}, Result: <200 OK OK,/uploads/messages/34e2b75d-a5b2-44fd-a2e8-e32ff2b59f9e.docx,[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:11:25', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (9, 3, 3, 'admin123456', 'POST /presentation/apply', '申请举办宣讲会', 'Exception: \r\n### Error updating database.  Cause: java.sql.SQLException: Field \'location\' doesn\'t have a default value\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationMapper.xml]\r\n### The error may involve com.gr.geias.repository.PresentationRepository.insertPresentation-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO tb_presentation (             company_id, title, capacity,             start_time, end_time, description, status, create_time, update_time         ) VALUES (                      ?, ?, ?,                      ?, ?, ?, ?, ?, ?                  )\r\n### Cause: java.sql.SQLException: Field \'location\' doesn\'t have a default value\n; Field \'location\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'location\' doesn\'t have a default value', '0:0:0:0:0:0:0:1', '2025-07-01 17:26:55', 0, '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'location\' doesn\'t have a default value\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationMapper.xml]\r\n### The error may involve com.gr.geias.repository.PresentationRepository.insertPresentation-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO tb_presentation (             company_id, title, capacity,             start_time, end_time, description, status, create_time, update_time         ) VALUES (                      ?, ?, ?,                      ?, ?, ?, ?, ?, ?                  )\r\n### Cause: java.sql.SQLException: Field \'location\' doesn\'t have a default value\n; Field \'location\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'location\' doesn\'t have a default value');
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (10, 3, 3, 'admin123456', 'POST /presentation/apply', '申请举办宣讲会', 'Exception: \r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE)\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationMapper.xml]\r\n### The error may involve com.gr.geias.repository.PresentationRepository.insertPresentation-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO tb_presentation (             company_id, title, capacity,             start_time, end_time, description, status, create_time, update_time         ) VALUES (                      ?, ?, ?,                      ?, ?, ?, ?, ?, ?                  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE)\n; Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE)', '0:0:0:0:0:0:0:1', '2025-07-01 17:28:05', 0, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE)\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationMapper.xml]\r\n### The error may involve com.gr.geias.repository.PresentationRepository.insertPresentation-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO tb_presentation (             company_id, title, capacity,             start_time, end_time, description, status, create_time, update_time         ) VALUES (                      ?, ?, ?,                      ?, ?, ?, ?, ?, ?                  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE)\n; Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`graduateemploymentinfo`.`tb_presentation`, CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE)');
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (11, 3, 3, 'admin123456', 'POST /presentation/apply', '申请举办宣讲会', 'Params: {}, Result: <200 OK OK,宣讲会申请成功，等待审核,[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:30:39', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (12, 3, 3, 'admin123456', 'POST /presentation/apply', '申请举办宣讲会', 'Params: {}, Result: <200 OK OK,宣讲会申请成功，等待审核,[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:35:04', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (13, 3, 3, 'admin123456', 'GET /presentation/company/1', '获取公司申请的宣讲会列表', 'Params: {}, Result: <200 OK OK,[Presentation(presentationId=3, companyId=1, title=2025届春招宣讲会, location=null, capacity=100, startTime=Sat Jul 05 10:00:00 CST 2025, endTime=Sat Jul 05 12:00:00 CST 2025, description=主要介绍公司招聘需求、岗位信息, status=0, remark=null, createTime=Tue Jul 01 17:35:04 CST 2025, updateTime=Tue Jul 01 17:38:29 CST 2025, signupCount=0)],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:38:52', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (14, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:41:07', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (15, 3, 3, 'admin123456', 'GET /presentation/admin/presentations', '获取所有宣讲会申请记录', 'Params: {}, Result: <200 OK OK,[],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:41:23', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (16, 3, 3, 'admin123456', 'GET /presentation/admin/presentations', '获取所有宣讲会申请记录', 'Params: {}, Result: <200 OK OK,[],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:42:31', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (17, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:45:53', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (18, 3, 3, 'admin123456', 'GET /presentation/admin/presentations', '获取所有宣讲会申请记录', 'Exception: \r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'c.unit.id\' in \'field list\'\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationMapper.xml]\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT         p.presentation_id,         p.title,         p.location,         p.capacity,         p.start_time,         p.end_time,         p.description,         p.status,         p.remark,         p.signup_count,         p.create_time,         p.update_time,         c.company_id AS companyId,         c.company_name AS companyName,         c.unit.id as companyType         FROM tb_presentation p         JOIN tb_company_info c ON p.company_id = c.company_id                    ORDER BY p.create_time DESC\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'c.unit.id\' in \'field list\'\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column \'c.unit.id\' in \'field list\'', '0:0:0:0:0:0:0:1', '2025-07-01 17:46:02', 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'c.unit.id\' in \'field list\'\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationMapper.xml]\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT         p.presentation_id,         p.title,         p.location,         p.capacity,         p.start_time,         p.end_time,         p.description,         p.status,         p.remark,         p.signup_count,         p.create_time,         p.update_time,         c.company_id AS companyId,         c.company_name AS companyName,         c.unit.id as companyType         FROM tb_presentation p         JOIN tb_company_info c ON p.company_id = c.company_id                    ORDER BY p.create_time DESC\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'c.unit.id\' in \'field list\'\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column \'c.unit.id\' in \'field list\'');
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (19, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:47:08', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (20, 3, 3, 'admin123456', 'GET /presentation/admin/presentations', '获取所有宣讲会申请记录', 'Params: {}, Result: <200 OK OK,[PresentationWithCompanyDTO(presentationId=3, title=2025届春招宣讲会, location=null, capacity=100, startTime=Sat Jul 05 10:00:00 CST 2025, endTime=Sat Jul 05 12:00:00 CST 2025, description=主要介绍公司招聘需求、岗位信息, status=0, remark=null, signupCount=0, createTime=Tue Jul 01 17:35:04 CST 2025, updateTime=Tue Jul 01 17:38:29 CST 2025, companyId=1, companyName=1, companyType=1)],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:47:16', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (21, 3, 3, 'admin123456', 'PUT /presentation/3/approve', '审批通过宣讲会申请', 'Params: {remark=[Ljava.lang.String;@2b3fdd6e, location=[Ljava.lang.String;@356f73c9}, Result: <200 OK OK,审批通过成功,[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:48:57', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (22, 3, 3, 'admin123456', 'POST /presentation/3/signup', '学生报名宣讲会', 'Params: {studentId=[Ljava.lang.String;@4be4a81f}, Result: <200 OK OK,报名成功！,[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:50:27', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (23, 3, 3, 'admin123456', 'GET /presentation/student/signed', '获取学生已报名宣讲会列表', 'Params: {studentId=[Ljava.lang.String;@4d34523c}, Result: <200 OK OK,[],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:51:18', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (24, 3, 3, 'admin123456', 'GET /presentation/student/signed', '获取学生已报名宣讲会列表', 'Params: {studentId=[Ljava.lang.String;@1ee915ec}, Result: <200 OK OK,[],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:51:22', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (25, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 17:59:18', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (26, 3, 3, 'admin123456', 'GET /presentation/student/signed', '获取学生已报名宣讲会列表', 'Params: {studentId=[Ljava.lang.String;@2d0bc46d}, Result: <200 OK OK,[PresentationWithCompanyDTO(presentationId=3, title=2025届春招宣讲会, location=B203, capacity=100, startTime=Sat Jul 05 10:00:00 CST 2025, endTime=Sat Jul 05 12:00:00 CST 2025, description=主要介绍公司招聘需求、岗位信息, status=1, remark=审核通过, signupCount=1, createTime=Tue Jul 01 17:35:04 CST 2025, updateTime=Tue Jul 01 17:50:27 CST 2025, companyId=1, companyName=1, companyType=国企)],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 17:59:27', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (27, 3, 3, 'admin123456', 'DELETE /presentation/student/cancel', '学生撤销宣讲会报名', 'Params: {studentId=[Ljava.lang.String;@61166a5, presentationId=[Ljava.lang.String;@146e8d3c}, Result: <200 OK OK,取消报名成功,[]>', '0:0:0:0:0:0:0:1', '2025-07-01 18:03:11', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (28, 3, 3, 'admin123456', 'GET /presentation/specialty/5', '获取宣讲会专业分布', 'Exception: \r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'p.class_id\' in \'on clause\'\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationSignupMapper.xml]\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT sp.specialty_name AS specialtyName, COUNT(*) AS count         FROM tb_presentation_signup ps             JOIN tb_person_info p ON ps.student_id = p.person_id             JOIN tb_class_grade c ON p.class_id = c.class_id             JOIN tb_specialty sp ON c.specialty_id = sp.specialty_id         WHERE ps.presentation_id = ?         GROUP BY sp.specialty_name\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'p.class_id\' in \'on clause\'\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column \'p.class_id\' in \'on clause\'', '0:0:0:0:0:0:0:1', '2025-07-01 18:03:45', 0, '\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'p.class_id\' in \'on clause\'\r\n### The error may exist in file [D:\\programstudy\\test\\employment\\employment\\backend\\target\\classes\\mappers\\PresentationSignupMapper.xml]\r\n### The error may involve defaultParameterMap\r\n### The error occurred while setting parameters\r\n### SQL: SELECT sp.specialty_name AS specialtyName, COUNT(*) AS count         FROM tb_presentation_signup ps             JOIN tb_person_info p ON ps.student_id = p.person_id             JOIN tb_class_grade c ON p.class_id = c.class_id             JOIN tb_specialty sp ON c.specialty_id = sp.specialty_id         WHERE ps.presentation_id = ?         GROUP BY sp.specialty_name\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'p.class_id\' in \'on clause\'\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column \'p.class_id\' in \'on clause\'');
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (29, 3, 3, 'admin123456', 'POST /api/personinfo/login', '用户登录', NULL, NULL, '2025-07-01 18:06:54', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (30, 3, 3, 'admin123456', 'GET /presentation/specialty/5', '获取宣讲会专业分布', 'Params: {}, Result: <200 OK OK,[],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 18:07:05', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (31, 3, 3, 'admin123456', 'POST /presentation/3/signup', '学生报名宣讲会', 'Params: {studentId=[Ljava.lang.String;@65108282}, Result: <200 OK OK,报名成功！,[]>', '0:0:0:0:0:0:0:1', '2025-07-01 18:07:27', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (32, 3, 3, 'admin123456', 'GET /presentation/specialty/5', '获取宣讲会专业分布', 'Params: {}, Result: <200 OK OK,[],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 18:07:31', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (33, 3, 3, 'admin123456', 'GET /presentation/specialty/5', '获取宣讲会专业分布', 'Params: {}, Result: <200 OK OK,[],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 18:09:27', 1, NULL);
INSERT INTO `operation_log` (`id`, `person_id`, `enable_status`, `username`, `operation_type`, `target`, `details`, `ip_address`, `operation_time`, `success`, `error_msg`) VALUES (34, 3, 3, 'admin123456', 'GET /presentation/specialty/3', '获取宣讲会专业分布', 'Params: {}, Result: <200 OK OK,[{specialtyName=软件工程, count=1}],[]>', '0:0:0:0:0:0:0:1', '2025-07-01 18:09:34', 1, NULL);
COMMIT;

-- ----------------------------
-- Table structure for tb_area
-- ----------------------------
DROP TABLE IF EXISTS `tb_area`;
CREATE TABLE `tb_area` (
  `area_id` int NOT NULL AUTO_INCREMENT,
  `area_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `parent_id` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`area_id`) USING BTREE,
  KEY `tb_area_parent` (`parent_id`) USING BTREE,
  CONSTRAINT `tb_area_parent` FOREIGN KEY (`parent_id`) REFERENCES `tb_area` (`area_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_area
-- ----------------------------
BEGIN;
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (4, '四川', NULL, '2020-03-06 13:39:59');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (5, '重庆', NULL, '2020-03-06 13:40:14');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (6, '成都', 4, '2020-03-06 13:46:34');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (8, '河北', NULL, '2020-03-06 23:35:56');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (9, '石家庄', 8, '2020-03-06 23:36:40');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (10, '广东', NULL, '2020-03-06 23:36:51');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (11, '广州', 10, '2020-03-06 23:36:59');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (12, '浙江', NULL, '2020-03-06 23:37:21');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (13, '杭州', 12, '2020-03-06 23:37:38');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (14, '湖北', NULL, '2020-03-06 23:38:06');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (15, '武汉', 14, '2020-03-06 23:38:17');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (16, '江苏', NULL, '2020-03-06 23:38:33');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (17, '南京', 16, '2020-03-06 23:38:45');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (18, '辽宁', NULL, '2020-03-06 23:39:21');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (19, '沈阳', 18, '2020-03-06 23:39:40');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (20, '湖南', NULL, '2020-03-06 23:39:55');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (21, '长沙', 20, '2020-03-06 23:40:03');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (22, '河南', NULL, '2020-03-06 23:40:22');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (23, '郑州', 22, '2020-03-06 23:40:38');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (24, '山东', NULL, '2020-03-06 23:40:48');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (25, '济南', 24, '2020-03-06 23:41:01');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (26, '黑龙江', NULL, '2020-03-06 23:41:18');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (27, '哈尔滨', 26, '2020-03-06 23:41:30');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (28, '吉林', NULL, '2020-03-06 23:41:39');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (29, '长春', 28, '2020-03-06 23:41:51');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (30, '陕西', NULL, '2020-03-06 23:42:04');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (31, '西安', 30, '2020-03-06 23:42:28');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (32, '福建', NULL, '2020-03-06 23:42:40');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (33, '福州', 32, '2020-03-06 23:42:50');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (34, '安徽', NULL, '2020-03-06 23:43:04');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (35, '合肥', 34, '2020-03-06 23:43:12');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (36, '江西', NULL, '2020-03-06 23:43:22');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (37, '南昌', 36, '2020-03-06 23:43:38');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (38, '云南', NULL, '2020-03-06 23:43:56');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (39, '昆明', 38, '2020-03-06 23:44:10');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (40, '内蒙古', NULL, '2020-03-06 23:44:22');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (41, '呼和浩特', 40, '2020-03-06 23:44:50');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (42, '广西', NULL, '2020-03-06 23:45:00');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (43, '南宁', 42, '2020-03-06 23:45:15');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (44, '山西', NULL, '2020-03-06 23:45:33');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (45, '太原', 44, '2020-03-06 23:45:45');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (46, '新疆', NULL, '2020-03-06 23:45:54');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (47, '乌鲁木齐', 46, '2020-03-06 23:46:07');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (48, '贵州', NULL, '2020-03-06 23:46:16');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (49, '贵阳', 48, '2020-03-06 23:46:28');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (50, '甘肃', NULL, '2020-03-06 23:46:38');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (51, '兰州', 50, '2020-03-06 23:46:53');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (52, '青海', NULL, '2020-03-06 23:47:04');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (53, '西宁', 52, '2020-03-06 23:47:15');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (54, '海南', NULL, '2020-03-06 23:47:27');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (55, '海口', 54, '2020-03-06 23:47:39');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (56, '宁夏', NULL, '2020-03-06 23:47:57');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (57, '银川', 56, '2020-03-06 23:48:10');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (58, '西藏', NULL, '2020-03-06 23:48:36');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (59, '拉萨', 58, '2020-03-06 23:48:46');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (60, '台湾', NULL, '2020-03-06 23:49:17');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (61, '台北', 60, '2020-03-06 23:49:25');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (62, '北京市', NULL, '2020-03-06 23:50:22');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (63, '上海市', NULL, '2020-03-06 23:50:34');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (64, '天津市', NULL, '2020-03-06 23:50:49');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (65, '深圳', 10, '2020-03-06 23:52:25');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (66, '香港', NULL, '2020-03-07 00:00:09');
INSERT INTO `tb_area` (`area_id`, `area_name`, `parent_id`, `create_time`) VALUES (67, '澳门', NULL, '2020-03-07 00:00:16');
COMMIT;

-- ----------------------------
-- Table structure for tb_class_grade
-- ----------------------------
DROP TABLE IF EXISTS `tb_class_grade`;
CREATE TABLE `tb_class_grade` (
  `class_id` int NOT NULL AUTO_INCREMENT,
  `class_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `specialty_id` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `admin_id` int DEFAULT NULL,
  PRIMARY KEY (`class_id`) USING BTREE,
  KEY `tb_class_grade_tb_specialty` (`specialty_id`) USING BTREE,
  KEY `tb_class_grade_tb_person_info` (`admin_id`) USING BTREE,
  CONSTRAINT `tb_class_grade_tb_person_info` FOREIGN KEY (`admin_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_class_grade_tb_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `tb_specialty` (`specialty_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_class_grade
-- ----------------------------
BEGIN;
INSERT INTO `tb_class_grade` (`class_id`, `class_name`, `specialty_id`, `create_time`, `admin_id`) VALUES (1, '软件工程2016级1班', 1, '2025-06-25 22:22:23', 3);
INSERT INTO `tb_class_grade` (`class_id`, `class_name`, `specialty_id`, `create_time`, `admin_id`) VALUES (2, '信息管理2016级1班', 2, '2025-06-25 22:22:23', 3);
COMMIT;

-- ----------------------------
-- Table structure for tb_college
-- ----------------------------
DROP TABLE IF EXISTS `tb_college`;
CREATE TABLE `tb_college` (
  `college_id` int NOT NULL AUTO_INCREMENT,
  `college_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `admin_id` int DEFAULT NULL,
  PRIMARY KEY (`college_id`) USING BTREE,
  KEY `tb_college_tb_person_info` (`admin_id`) USING BTREE,
  CONSTRAINT `tb_college_tb_person_info` FOREIGN KEY (`admin_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_college
-- ----------------------------
BEGIN;
INSERT INTO `tb_college` (`college_id`, `college_name`, `create_time`, `admin_id`) VALUES (1, '计算机学院', '2025-06-25 22:22:23', 3);
INSERT INTO `tb_college` (`college_id`, `college_name`, `create_time`, `admin_id`) VALUES (2, '管理学院', '2025-06-25 22:22:23', 3);
COMMIT;

-- ----------------------------
-- Table structure for tb_company_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_company_info`;
CREATE TABLE `tb_company_info` (
  `company_id` int NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `company_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '企业名称',
  `company_intro` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '企业简介',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '企业地址',
  `area_id` int DEFAULT NULL COMMENT '所属地区',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `unit_id` int DEFAULT NULL COMMENT '企业类型ID',
  `confirmed` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`company_id`) USING BTREE,
  KEY `area_id` (`area_id`) USING BTREE,
  KEY `unit_id` (`unit_id`) USING BTREE,
  CONSTRAINT `fk_company_area` FOREIGN KEY (`area_id`) REFERENCES `tb_area` (`area_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_company_unit` FOREIGN KEY (`unit_id`) REFERENCES `tb_unit_kind` (`unit_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='参会企业信息表';

-- ----------------------------
-- Records of tb_company_info
-- ----------------------------
BEGIN;
INSERT INTO `tb_company_info` (`company_id`, `company_name`, `company_intro`, `address`, `area_id`, `create_time`, `unit_id`, `confirmed`) VALUES (1, '1', '1', '1', NULL, '2025-06-29 13:28:31', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for tb_employment_information
-- ----------------------------
DROP TABLE IF EXISTS `tb_employment_information`;
CREATE TABLE `tb_employment_information` (
  `information_id` int NOT NULL AUTO_INCREMENT,
  `student_num` int NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `class_id` int NOT NULL,
  `area_id` int NOT NULL,
  `unit_id` int NOT NULL,
  `salary` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `employment_way_id` int NOT NULL,
  `msg` varchar(10000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `gender` int NOT NULL,
  `college_id` int NOT NULL,
  `specialty_id` int NOT NULL,
  PRIMARY KEY (`information_id`) USING BTREE,
  UNIQUE KEY `student_num_qurey` (`student_num`) USING BTREE,
  KEY `tb_employment_information_tb_class_grade` (`class_id`) USING BTREE,
  KEY `tb_employment_information_tb_area` (`area_id`) USING BTREE,
  KEY `tb_employment_information_tb_unit_kind` (`unit_id`) USING BTREE,
  KEY `tb_employment_information_tb_employment_way` (`employment_way_id`) USING BTREE,
  KEY `tb_employment_information_tb_college` (`college_id`) USING BTREE,
  KEY `tb_employment_information_tb_specialty` (`specialty_id`) USING BTREE,
  CONSTRAINT `tb_employment_information_tb_area` FOREIGN KEY (`area_id`) REFERENCES `tb_area` (`area_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_class_grade` FOREIGN KEY (`class_id`) REFERENCES `tb_class_grade` (`class_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_college` FOREIGN KEY (`college_id`) REFERENCES `tb_college` (`college_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_employment_way` FOREIGN KEY (`employment_way_id`) REFERENCES `tb_employment_way` (`employment_way_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `tb_specialty` (`specialty_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_unit_kind` FOREIGN KEY (`unit_id`) REFERENCES `tb_unit_kind` (`unit_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=1036 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_employment_information
-- ----------------------------
BEGIN;
INSERT INTO `tb_employment_information` (`information_id`, `student_num`, `name`, `class_id`, `area_id`, `unit_id`, `salary`, `employment_way_id`, `msg`, `create_time`, `gender`, `college_id`, `specialty_id`) VALUES (1033, 20200101, '张三', 1, 6, 1, '8000', 2, '工作地点在成都', '2025-06-25 22:22:23', 1, 1, 1);
INSERT INTO `tb_employment_information` (`information_id`, `student_num`, `name`, `class_id`, `area_id`, `unit_id`, `salary`, `employment_way_id`, `msg`, `create_time`, `gender`, `college_id`, `specialty_id`) VALUES (1034, 20200102, '李四', 1, 9, 2, '7000', 1, '河北老家工作', '2025-06-25 22:22:23', 0, 1, 1);
INSERT INTO `tb_employment_information` (`information_id`, `student_num`, `name`, `class_id`, `area_id`, `unit_id`, `salary`, `employment_way_id`, `msg`, `create_time`, `gender`, `college_id`, `specialty_id`) VALUES (1035, 20200103, '王五', 2, 11, 3, '9000', 3, '公务员编制', '2025-06-25 22:22:23', 1, 2, 2);
COMMIT;

-- ----------------------------
-- Table structure for tb_employment_way
-- ----------------------------
DROP TABLE IF EXISTS `tb_employment_way`;
CREATE TABLE `tb_employment_way` (
  `employment_way_id` int NOT NULL AUTO_INCREMENT,
  `vay_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`employment_way_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_employment_way
-- ----------------------------
BEGIN;
INSERT INTO `tb_employment_way` (`employment_way_id`, `vay_name`, `create_time`) VALUES (1, '学校双选会', '2020-03-06 13:57:09');
INSERT INTO `tb_employment_way` (`employment_way_id`, `vay_name`, `create_time`) VALUES (2, '互联网招聘平台', '2020-03-06 13:57:27');
INSERT INTO `tb_employment_way` (`employment_way_id`, `vay_name`, `create_time`) VALUES (3, '个人寻找', '2020-03-06 13:57:45');
INSERT INTO `tb_employment_way` (`employment_way_id`, `vay_name`, `create_time`) VALUES (4, '朋友推荐', '2020-03-06 13:57:59');
COMMIT;

-- ----------------------------
-- Table structure for tb_interview
-- ----------------------------
DROP TABLE IF EXISTS `tb_interview`;
CREATE TABLE `tb_interview` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `position_id` bigint NOT NULL COMMENT '职位ID',
  `status` int DEFAULT '1' COMMENT '面试状态：1-待面试，2-面试中，3-面试通过，4-面试未通过，5-已取消',
  `remarks` varchar(500) DEFAULT NULL COMMENT '面试备注/反馈',
  `interview_time` datetime DEFAULT NULL COMMENT '面试时间',
  `location` varchar(255) DEFAULT NULL COMMENT '面试地点',
  `interviewer` varchar(100) DEFAULT NULL COMMENT '面试官',
  `is_synced` tinyint DEFAULT '0' COMMENT '是否已同步到面试系统：0-未同步，1-已同步',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_resume_id` (`resume_id`),
  KEY `idx_position_id` (`position_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_synced` (`is_synced`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='面试信息表';

-- ----------------------------
-- Records of tb_interview
-- ----------------------------
BEGIN;
INSERT INTO `tb_interview` (`id`, `resume_id`, `position_id`, `status`, `remarks`, `interview_time`, `location`, `interviewer`, `is_synced`, `create_time`, `update_time`) VALUES (4, 1, 1, 3, '面试已完成', '2024-07-01 18:00:00', '线上面试', '面试官A', 1, '2025-06-30 10:18:45', '2025-06-30 11:04:38');
INSERT INTO `tb_interview` (`id`, `resume_id`, `position_id`, `status`, `remarks`, `interview_time`, `location`, `interviewer`, `is_synced`, `create_time`, `update_time`) VALUES (5, 1, 1, 3, '面试已完成', '2025-07-01 18:00:00', NULL, NULL, 1, '2025-06-30 10:36:53', '2025-06-30 10:57:54');
INSERT INTO `tb_interview` (`id`, `resume_id`, `position_id`, `status`, `remarks`, `interview_time`, `location`, `interviewer`, `is_synced`, `create_time`, `update_time`) VALUES (6, 1, 1, 2, '面试进行中', '2025-07-02 18:00:00', NULL, NULL, 1, '2025-06-30 10:44:29', '2025-06-30 10:48:29');
INSERT INTO `tb_interview` (`id`, `resume_id`, `position_id`, `status`, `remarks`, `interview_time`, `location`, `interviewer`, `is_synced`, `create_time`, `update_time`) VALUES (7, 1, 1, 1, '请携带简历和身份证', NULL, '北京市海淀区科技大厦15楼', NULL, 0, '2025-07-02 11:25:20', '2025-07-02 11:25:20');
COMMIT;

-- ----------------------------
-- Table structure for tb_job_fair
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_fair`;
CREATE TABLE `tb_job_fair` (
  `job_fair_id` int NOT NULL AUTO_INCREMENT COMMENT '招聘会ID',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '招聘会主题',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '招聘会简介',
  `location` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '举办地点',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `organizer_id` int DEFAULT NULL COMMENT '主办人ID（管理员或教师）',
  `booth_total` int DEFAULT '0' COMMENT '可用展位总数',
  PRIMARY KEY (`job_fair_id`) USING BTREE,
  KEY `organizer_id` (`organizer_id`) USING BTREE,
  CONSTRAINT `fk_job_fair_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='招聘会信息表';

-- ----------------------------
-- Records of tb_job_fair
-- ----------------------------
BEGIN;
INSERT INTO `tb_job_fair` (`job_fair_id`, `title`, `description`, `location`, `start_time`, `end_time`, `create_time`, `organizer_id`, `booth_total`) VALUES (1, '2025届校园招聘会', '本次招聘会汇聚优质企业，欢迎同学们积极参与。', '中南大学双创中心', '2025-06-10 09:00:00', '2025-06-11 16:00:00', '2025-06-29 09:04:21', 3, 50);
COMMIT;

-- ----------------------------
-- Table structure for tb_job_fair_company
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_fair_company`;
CREATE TABLE `tb_job_fair_company` (
  `job_fair_id` int NOT NULL COMMENT '招聘会ID',
  `company_id` int NOT NULL COMMENT '企业ID',
  `booth_location` int DEFAULT NULL COMMENT '展位位置',
  `status` int DEFAULT '0' COMMENT '申请状态：0-待审核，1-通过，2-拒绝',
  PRIMARY KEY (`job_fair_id`,`company_id`) USING BTREE,
  KEY `fk_jfc_company` (`company_id`) USING BTREE,
  CONSTRAINT `fk_jfc_company` FOREIGN KEY (`company_id`) REFERENCES `tb_company_info` (`company_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_jfc_job_fair` FOREIGN KEY (`job_fair_id`) REFERENCES `tb_job_fair` (`job_fair_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='招聘会-参会企业关联表';

-- ----------------------------
-- Records of tb_job_fair_company
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for tb_job_position
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_position`;
CREATE TABLE `tb_job_position` (
  `job_id` int NOT NULL AUTO_INCREMENT,
  `company_id` int NOT NULL COMMENT '企业ID',
  `title` varchar(100) NOT NULL COMMENT '岗位标题',
  `description` text COMMENT '岗位描述',
  `requirements` text COMMENT '任职要求',
  `salary_range` varchar(50) DEFAULT NULL COMMENT '薪资范围',
  `location` varchar(100) DEFAULT NULL COMMENT '工作地点',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-下架，1-发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`job_id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';

-- ----------------------------
-- Records of tb_job_position
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for tb_job_posting
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_posting`;
CREATE TABLE `tb_job_posting` (
  `position_id` int NOT NULL,
  `position_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `city` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `salary` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `company_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `company_tags` json DEFAULT NULL,
  `education_requirement` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `experience_required` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `job_id` int NOT NULL,
  `recruit_number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `preferred_majors` json DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `job_description` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `job_place` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`job_id`,`position_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_job_posting
-- ----------------------------
BEGIN;
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (1, '管培生-营销运营', '「泰州·泰兴市」', '8000-12000元', '济川药业集团有限公司', '[\"医药制造\", \"1000-9999人\", \"上市公司\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-05-16 08:00:00', '岗位职责:\n1、根据市场发展，起草营销政策。同时对政策制定后的达成效果进行分析和纠偏；\n2、对营销线重点工作计划进行跟踪（目标制定，事项达成进度跟进，结果考核）；\n3、会议管理 （会议预约、会议布置及各项准备工作）；\n4、协同事项：营销线重点事项进度跟踪及复盘、市场问题处理、产供销会议、绩效报告书事项。\n\n\n任职要求:\n1、本科及以上学历，医药、市场营销、管理类等相关专业；\n2、具备良好的沟通能力和人际交往能力，能够与他人建立良好的合作关系；\n3、具备项目管理的经验＆能力；\n4、熟练掌握Excel＆PPT等办公软件；\n5、具备较强的学习能力和适应能力；能够快速适应公司文化和岗位要求。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (2, '计算机信息类', '「徐州」', '面议', '徐州矿务集团有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招8人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：计算机科学与技术、电子信息工程、网络工程等', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (3, '环境科学岗', '「无锡·滨湖」', '面议', '中国船舶集团有限公司', '[\"船舶/航空/航天/火车制造\", \"10000人以上\", \"国企\"]', '本科', '经验不限', 373, '若干', '[\"环境科学与工程\"]', '2025-04-07 08:00:00', '备注：该岗位为我单位下属子公司招聘需求。\n岗位要求：\n1. 本科及以上学历，环境科学、环境工程或相关专业\n2. 具备良好的分析和解决问题的能力，能够独立处理技术难题。\n3. 有良好的沟通和团队协作能力，能够与跨部门团队有效合作。\n4. 具备较强的学习能力和适应能力，能够快速掌握新技术和新方法。', '山水东路');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (4, '管理培训生', '「上海·浦东」', '1.7-2.5万', '上港集团', '[\"货运/物流/仓储\", \"100-299人\", \"上市公司\"]', '本科', '无经验', 373, '招2人', '[\"信息暂无\"]', '2025-03-06 08:00:00', '岗位内容:\n1.参与公司经营管理、业务流程优化、科技创新项目推进等工作。\n2.参与企业管理规范和内部流程优化。\n培养方向:\n企业经营管理、专业技术管理、综合管理等高级管理人员。\n任职要求:\n1.优秀院校本科及以上学历，有出色的团队协作和执行力。\n2.出色的逻辑分析能力和数据驱动思维，对业务流程改进有敏锐的嗅觉。\n3.勤奋上进，具备较强的抗压能力和问题解决能力。\n工作地点：临港新片区洋山3期码头（公司提供上下班提供班车）\n薪酬福利：公司提供行业内一流的薪酬待遇，除了国家规定的五险一金以外，还提供补充公积金、企业年金（补充养老金）、补充医保、交通补贴、差旅补贴、员工体检、生日礼物、带薪休假、中超球迷协会等福利待遇及工会福利。公司员工符合条件者，还可以享受临港新片区人才落户、人才购房及租房等各类优惠政策。', '上海冠东国际集装箱码头有限公司');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (5, '机器人测试工程师', '「上海·普陀」', '8000-16000元·14薪', '上海电器科学研究所（集团）有限公司', '[\"专业技术服务\", \"1000-9999人\", \"国企\"]', '本科', '无经验', 373, '招10人', '[\"控制理论与控制工程, 电机与电力电子, 电子信息工程, 机器人工程, 计算机科学与技术, 软件工程\"]', '2025-04-10 08:00:00', '机器人功能测试、数据采集工作', '上海电器科学研究院(武宁路)');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (6, '管培生-人力资源', '「上海」', '面议', '济川药业集团有限公司', '[\"医药制造\", \"1000-9999人\", \"上市公司\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-04-11 08:00:00', '岗位职责:\n1、人力资源规划与策略：协助为公司制定与战略目标相匹配的人力资源策略和规划；\n2、招聘：负责应届毕业生招聘工作；协助成熟型人才的招聘；协助招聘制度与流程的优化执行；\n3、HRBP：协助业务中心团队绩效管理、员工关系管理及部分人才项目落地；\n4、薪酬与福利管理：参与制定薪酬福利体系及相关政策，完善和优化薪酬福利体系；\n5、培训与发展：负责社招NEO培训的组织实施，并跟进培训效果；协助营销培训的组织、测评、跟进；协助人才发展项目的开展与跟踪；负责部分课程的开发与授课。\n\n\n任职要求:\n1、本科及以上学历，人力资源管理、工商管理、心理学等相关专业优先；\n2、较强的学习能力和责任心，工作积极主动，思维敏捷，逻辑思维能力出色；\n3、熟练使用excel、PowerPoint等办公软件，具备较强的语言表达、数据分析、公文撰写等能力。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (7, '行政助理', '「徐州」', '面议', '徐州国力电力设备有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：不限专业', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (8, '环境工程', '「泰州」', '5000-10000元', '江苏杭富环保科技有限公司', '[\"环保\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '招6人', '[\"材料类\"]', '2025-04-24 08:00:00', '经验要求不限，专业要求：地质类,环境科学与工程类,经济学类,生物科学类', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (9, '管理培训生', '「上海·浦东」', '1.7-2.5万', '上港集团', '[\"货运/物流/仓储\", \"100-299人\", \"上市公司\"]', '本科', '无经验', 373, '招2人', '[\"信息暂无\"]', '2025-04-14 08:00:00', '岗位内容:\n1.参与公司经营管理、业务流程优化、科技创新项目推进等工作。\n2.参与企业管理规范和内部流程优化。\n培养方向:\n企业经营管理、专业技术管理、综合管理等高级管理人员。\n任职要求:\n1.优秀院校本科及以上学历，有出色的团队协作和执行力。\n2.出色的逻辑分析能力和数据驱动思维，对业务流程改进有敏锐的嗅觉。\n3.勤奋上进，具备较强的抗压能力和问题解决能力。', '上海冠东国际集装箱码头有限公司');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (10, '医院质量管理', '「丽水」', '1-1.2万', '龙泉市人民医院（浙江大学附属第二医院龙泉分院）', '[\"社团/组织/社会保障\", \"100-299人\", \"其它\"]', '本科', '经验不限', 373, '招1人', '[\"公共卫生管理\"]', '2025-03-20 08:00:00', '35周岁及以下（1988年3月26日以后出生）,本科及以上学历，专业要求：公共卫生管理、卫生事业管理、医学信息学、健康服务与管理、健康管理', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (11, '管培生-总裁办', '「上海」', '面议', '济川药业集团有限公司', '[\"医药制造\", \"1000-9999人\", \"上市公司\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-05-14 08:00:00', '岗位职责:\n1、协助跟进集团级别的项目、流程；\n2、协助开展集团体系化建设工作，完善与优化管理标准，提升管理效能；\n3、协助组织集团各类管理专项工作，确保管理专项任务有效落地；\n4、协助组织召开集团重大运营/管理会议，输出会议纪要，并督办会议任务达成；\n5、协助编制各类文字方案与材料，包括策划方案、政府呈文、公司报告、发言材料等；\n6、上级安排的其它工作。\n\n\n任职要求:\n1、本科及以上学历，药学、医学等相关专业；\n2、熟练操作Office软件；\n3、具有较好的逻辑思维、文字撰写与组织协调能力；\n4、具备一定的抗压能力，能够适应高强度的工作环境；\n5、具有较强的学习能力与沟通能力；\n6、具有医药公司实习经历优先。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (12, '城市经理', '「徐州」', '面议', '维维食品饮料股份有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：不限，市场营销等相关专业优先', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (13, '金融管培生', '「上海·徐汇」', '1-1.5万·15薪', '上海孚厘科技有限公司', '[\"互联网\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '若干', '[\"金融学, 经济学, 财务管理, 市场营销\"]', '2025-05-13 08:00:00', '该岗位面向2025及2026届应届毕业生，旨在为公司一线营销团队培养管理人才。\n\n岗位职责：\n1.第一年需拓展客户资源，建立和维护目标客户群的业务关系，开展客户资信调查及资产保全工作；\n2.与相关业务方沟通，负责跟进内外部业务相关问题，确保项目高效高质量上线；\n3.轮岗过程中关注业务痛点及行业动态，参与相关行业规则制定和产品规划，主动优化打磨产品，不断提升产品价值上限；\n4.第一年销售轮岗后，根据履职表现及公司实际业务需要，结合个人发展意愿与擅长方向可转岗中后台职能部门（风控/运营/产品/数科等）。\n\n任职要求：\n1.本科及以上学历，金融或财务相关专业，仅限应届生或往届生（无金融从业背景）；\n2.学习力强，积极向上，希望和一群文化价值观正确、正直、进取的人一起奋斗；\n3.喜欢与人交流，有强烈成就动机，敢于挑战高薪，认可付出等于收获的理念；\n4.性格坚韧，战斗力超强，具备在困难、挫折、艰苦的逆境中生存的能力。', '星扬西岸中心2709-2712');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (14, '投资管理', '「徐州」', '面议', '徐州云盛建设发展投资集团有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：金融学、投资学、经济学、项目管理、财务管理等相关专业。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (15, 'QC检验员', '「徐州」', '面议', '江苏景泽生物制药有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招4人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：药学、食品药品、生物类相关专业', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (16, '助理工程师', '「徐州」', '面议', '强茂半导体（徐州有限公司）', '[\"通用设备制造\", \"500-999人\", \"港澳台公司\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：电子信息专业', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (17, '数据分析师', '「徐州」', '面议', '非凸智能科技（徐州）有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：数学、统计、大数据、财管、金融等理科专业', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (18, '有机合成研发员', '「泰州」', '7000-14000元', '江苏广域化学有限公司', '[\"化学原料/化学制品\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '招6人', '[\"材料类\"]', '2025-04-24 08:00:00', '经验要求不限，专业要求：化学相关专业', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (19, '理赔类岗位', '「徐州」', '面议', '中国人民财产保险股份有限公司徐州市分公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招3人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：车辆工程、交通运输、医学、法律、保险、金融、建筑、化工、机械、工程等相关专业。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (20, '管理培训生', '「南通·通州」', '7000-14000元·14薪', '海星股份', '[\"电子/半导体/集成电路\", \"500-999人\", \"上市公司\"]', '本科', '经验不限', 373, '若干', '[\"信息暂无\"]', '2025-06-05 08:00:00', '围绕公司人才梯队培养总体要求，学习、实践、成长，成为公司骨干。\n1、完成轮岗学习和相关工作任务；\n2、完成阶段性安排的项目工作；\n3、过程中明确定位、快速成长。\n岗位方向包括：投资、销售、供应链管理、生产管理、人资及行政管理', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (21, '2025届虹途生—设备管培生(J14994)', '「扬州·仪征市」', '面议', '东方雨虹', '[\"建材\", \"10000人以上\", \"上市公司\"]', '本科', '无经验', 373, '招5人', '[\"信息暂无\"]', '2025-03-26 08:00:00', '工作职责：\n1、全面管理工厂（聚合物乳液、胶粉车间）水、电、气等能耗管理，维护设备稳定运行，执行公司相关制度；\n2、负责设备台账管理，掌握全部电气运行情况；\n3、推进工厂年、季、月检修计划、编制设备大修计划；\n4、建立备品备件安全库存，合理控制备品所占用资金；\n5、认真抓好电气运行情况，出现故障及时维修，制定防范措施；\n6、负责新设备电气线路的安装、程序调试；\n7、组织设备部人员进行电气培训；\n8、上级领导交办的其他工作。\n任职资格：\n1、统招本科及以上学历，电气自动化、机械、电仪类相关专业；\n2、吃苦耐劳，擅于发现并分析解决问题，刻苦钻研，具备创新意识。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (22, '助理工程师', '「徐州」', '面议', '汉御微传感器（江苏）有限公司', '[\"通用设备制造\", \"500-999人\", \"其它\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：电子科学与技术，电子信息，计算机应用、电气自动化、微电子、集成电路等', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (23, '美术老师', '「徐州」', '面议', '沛县汉城文昌学校', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招14人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：美术老师', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (24, '山姆门店管培生-杭州', '「杭州」', '面议', '沃尔玛中国', '[\"零售/批发\", \"10000人以上\", \"外商独资\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-04-29 08:00:00', '● 管理培训生项目简介：\n本项目是为了培养山姆会员店的门店未来管理者而设立，项目为期2-3年，管培生将通过轮岗、跨部门学习体验、系统化领导力及岗位培训，参与制定销售策略、落实门店运营管理、业绩管理等业务，快速成长为山姆会员店的门店副总经理。\n\n● 岗位职责：\n1. 零售新星-岗位基础业务培训：在门店各部门进行轮岗学习，熟悉一线业务，了解零售运作机制，熟习并掌握部门主管岗位所需知识和技能。熟悉公司各项合法合规要求。\n2. 部门操盘手-部门主管岗位工作：\n(1) 负责部门业务管理，制定和落实工作计划，带领团队完成各项指标，如销售预算等。\n(2) 组织和制定人员培养发展计划，有效调配人力资源，提升部门效率，同时培养有发展潜力的员工。\n(3) 在部门内营造公平透明的沟通氛围，实践公仆领导，不断提高员工士气，实施和监督人员发展培训计划，有计划性地激励和提高员工的专业水平，建立高效团队。\n(4) 遵守并培训团队遵守政策及法律法规，推动落实各项工作标准和程序，确保达到门店运营的安全标准。\n3. 储备领航人-门店见习副总：系统学习并实践门店副总经理的职责及工作内容。\n4. 分区领航人-门店副总：通过测评将成为门店副总经理，负责门店分区即多个部门的业务和人员管理。\n\n● 任职要求：\n1. 本科及以上应届毕业生，专业不限。\n2. 热爱零售行业，积极主动，有强烈的服务意识、团队合作意识，较强学习能力、沟通能力以及领导力。\n3. 能适应倒班，接受工作地调动。\n\n● 工作时间：\n1. 每周标准工作时间为40小时。\n2. 排班时间根据门店业务需求制定，有不同时间段的班次，比如早班和晚班，具体时间灵活安排，无固定项。\n3. 因行业特性，一般在周一至周五排休，周末和节假日需正常上班。\n\n● 工作地点：\n在山姆会员店的门店工作。优先志愿城市，但会根据公司现有门店的人员需求进行调配，最终工作城市和地点可能会调整。\n\n● 薪酬福利：\n包含管培生工资进阶计划、年底双薪、五险一金、带薪年休假、节日福利、员工折扣卡、山姆会员卡。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (25, '外派管培生', '「苏州·吴江」', '1.5-2万', '亨通集团有限公司', '[\"通信/网络设备\", \"10000人以上\", \"民营\"]', '本科', '经验不限', 373, '招2人', '[\"电气工程及自动化\"]', '2025-03-21 08:00:00', '外派管培生适用特殊定制的培训计划，直接在12家海外产业公司培训，涉及海外产业公司人资、财务、行政、供应链、中台制造执行等模块培训，培养方向为海外产业公司中高层管理人员，薪酬定制。\n\n欢迎意向在海外长期发展、提升国际化能力、拓宽全球化视野的同学投递！', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (26, '研发', '「徐州」', '面议', '晶斐（徐州）半导体科技有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：材料科学与工程', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (27, '物资采购', '「徐州」', '面议', '徐州矿务（集团)新疆天山矿业有限责任公司阿克苏热电分公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招7人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：工商管理及相关专业', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (28, '测绘工程', '「徐州」', '面议', '江苏苏北土地房地产资产评估测绘咨询有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：不限专业', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (29, '设备技术员', '「泰州」', '7000-14000元', '江苏申源集团', '[\"电子设备制造\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-04-24 08:00:00', '经验要求不限，专业要求：电气及其自动化、机械设计制造及自动化、机械工程、能源与动力工程', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (30, '管培生（人力资源方向）', '「上海·闵行」', '1-1.5万', '爱琴海集团', '[\"物业管理\", \"500-999人\", \"合资\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2024-10-15 08:00:00', '工作职责：\n该职能负责公司的人力资源规划、人才招聘、人才发展、薪酬福利、绩效管理、员工管理等方面工作，通过提供领先的方法论和最优的解决方案，提升组织和人员能力，传承公司核心价值观\n任职资格：\n1、2025届应届本科及以上学历，专业不限；\n2、对商业领域有浓厚兴趣，愿意在此行业长期发展；\n3、具有优秀的商业兴趣和洞察力、较强的逻辑思维能力、敢于创新的探索精神、优秀的团队合作与沟通能力，不断寻求自我突破；\n4、具备一定的地域灵活性。（培养期内总部&项目多地域轮岗，后期结合个人意愿及公司发展确定工作地点）', '上海市闵行区申长路1466弄');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (31, '信息技术', '「徐州」', '面议', '沛县兴蓉水务发展有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招3人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：信息技术', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (32, '病案信息管理', '「丽水」', '1-1.2万', '龙泉市人民医院（浙江大学附属第二医院龙泉分院）', '[\"社团/组织/社会保障\", \"100-299人\", \"其它\"]', '本科', '经验不限', 373, '招1人', '[\"临床医学\"]', '2025-03-20 08:00:00', '35周岁及以下（1988年3月26日以后出生）,本科及以上学历，专业要求：临床医学类，卫生事业管理', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (33, 'AI产品经理', '「上海」', '面议', '上海复星高科技（集团）有限公司', '[\"零售/批发\", \"10000人以上\", \"上市公司\"]', '本科', '经验不限', 373, '若干', '[\"信息暂无\"]', '2025-04-23 08:00:00', '职位描述\n1. 参与AI技术在各产业（健康/消费/金融/制造等）的场景挖掘，完成需求分析、原型设计及产品落地；\n2. 协同算法与工程团队，推动AI技术转化为可落地的解决方案（如智能客服、AI辅助诊断、营销自动化工具等）；\n3. 跟踪AI行业趋势与竞品动态，输出产品迭代策略与商业化路径规划；\n4. 负责用户反馈收集、数据分析及产品效果评估，持续优化用户体验；\n5. 协助完成产品文档撰写、跨部门沟通及项目管理。\n\n职位要求\n硬性要求：\n1. 本科及以上学历，计算机、软件工程、信息管理、工业设计等相关专业（理工科背景优先）；\n2. 了解AI技术基础（如机器学习、NLP、CV等），熟悉主流AI平台工具（如Hugging Face、Azure AI等）；\n3. 具备产品思维，擅长逻辑分析及需求拆解，有产品设计/项目管理实习经验者优先；\n4. 熟练使用Axure/Figma/Sketch等原型工具，掌握SQL/Excel基础数据分析能力；\n5. 对AI技术商业化有强烈兴趣，关注AI+行业（如AIGC、智能硬件、产业数字化）创新场景。\n\n加分项：\n- 参与过AI产品全生命周期项目（需求-设计-上线-运营）；\n- 熟悉产品商业化模式（如SaaS、API服务、硬件集成）；\n- 有健康/消费/金融/制造等行业研究或实习经历，能快速理解业务痛点；\n- 具备基础编程能力（Python/JavaScript），可与技术团队高效协作。\n\n软技能要求：\n1. 用户导向：能从业务场景出发设计产品，平衡技术可行性与用户体验；\n2. 沟通能力：擅长跨团队协作，能将技术语言转化为业务价值；\n3. 商业敏感度：对AI产品的市场价值、成本收益有基础认知。', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (34, '化工类', '「徐州」', '面议', '徐州矿务集团有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招6人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：应用化学、高分子材料与工程等', '信息暂无');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (35, '运营管培生', '「无锡·锡山」', '8000-10000元·13薪', '确成股份', '[\"化工\", \"100-299人\", \"股份制企业\"]', '本科', '无经验', 373, '招10人', '[\"材料化学, 分析化学\"]', '2025-04-22 08:00:00', '1、学习与培养：接受公司的培训，学习公司战略、文化、经营模式和管理理念。通过内部培训课程、研讨会和导师指导等方式，了解公司的各个职能部门、流程、和运营模式，并学习解决问题和决策的基本技能。\n2、轮岗实践：参与各个只能部门的轮岗实践。在一定的时间内在不同的职位上工作，深入了解各部门的工作内容和流程，并从实践中学习解决问题的能力和跨部门协作的能力。', '确成硅化学股份有限公司青港路25号');
INSERT INTO `tb_job_posting` (`position_id`, `position_name`, `city`, `salary`, `company_name`, `company_tags`, `education_requirement`, `experience_required`, `job_id`, `recruit_number`, `preferred_majors`, `update_date`, `job_description`, `job_place`) VALUES (36, '体育老师', '「徐州」', '面议', '沛县汉城文昌学校', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招15人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：体育老师', '信息暂无');
COMMIT;

-- ----------------------------
-- Table structure for tb_news
-- ----------------------------
DROP TABLE IF EXISTS `tb_news`;
CREATE TABLE `tb_news` (
  `news_id` int NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '新闻标题',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '新闻正文',
  `image_urls` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '新闻图片URL（多个图片以 | 隔开）',
  `author_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '文章作者（如记者）',
  `publisher_id` int DEFAULT NULL COMMENT '发布者ID（关联tb_person_info）',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `read_count` int DEFAULT '0' COMMENT '阅读量',
  `tags` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '新闻标签（如校园新闻、就业动态等）',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标志（0=正常，1=已删除）',
  PRIMARY KEY (`news_id`) USING BTREE,
  KEY `idx_publisher` (`publisher_id`) USING BTREE,
  CONSTRAINT `tb_news_tb_person_info` FOREIGN KEY (`publisher_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='新闻资讯表';

-- ----------------------------
-- Records of tb_news
-- ----------------------------
BEGIN;
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (21, '校园招聘会成功举办', '我校于本周成功举办大型校园招聘会，吸引数十家企业参与，提供上千岗位。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '李华', 3, '2025-06-30 10:40:12', '2025-06-30 15:31:25', 122, '校园新闻', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (22, '计算机学院举办技术讲座', '讲座涵盖AI、大数据等前沿领域，吸引大量学生参与。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '王敏', 3, '2025-06-30 10:40:12', '2025-07-01 09:28:55', 87, '学术交流', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (23, '校企合作新突破', '我校与XX科技公司签署实习与科研合作协议。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '张伟', 3, '2025-06-30 10:40:12', '2025-06-30 14:59:53', 135, '就业动态', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (24, '毕业生就业率再创新高', '2025届毕业生整体就业率达98.6%，创历史新高。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '陈晨', 3, '2025-06-30 10:40:12', '2025-07-01 08:07:15', 209, '就业动态', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (25, '新生入学教育顺利开展', '2025级新生参加为期一周的入学教育和军训。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '刘洋', 3, '2025-06-30 10:40:12', '2025-06-30 15:39:07', 75, '校园新闻', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (26, '图书馆新增阅读空间', '图书馆增设安静自习区，为学生学习提供良好环境。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '周婷', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 42, '校园建设', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (27, '人工智能实验室落成', '我校新建AI实验室正式投入使用，服务相关课程教学。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '赵强', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 90, '教学科研', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (28, '大学生创新创业大赛开幕', '激发创新精神，培养创业能力，大赛精彩纷呈。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '孙悦', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 112, '校园活动', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (29, '毕业设计展圆满结束', '艺术学院举办毕业展，展示学生四年成果。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '李想', 3, '2025-06-30 10:40:12', '2025-06-30 14:16:43', 78, '艺术设计', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (30, '优秀校友回校讲学', '我校杰出校友回母校分享职场经验。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '丁磊', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 88, '校友动态', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (31, '软件学院组织企业参观', '师生一同参观合作企业，深入了解工作流程。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '刘娜', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 93, '就业实践', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (32, '学院举办简历制作培训', '指导学生完善简历，提高求职竞争力。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '马龙', 3, '2025-06-30 10:40:12', '2025-07-01 09:25:41', 68, '就业服务', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (33, '心理健康月活动启动', '开展各类心理讲座与咨询活动，关注学生心理健康。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '何晶', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 47, '校园文化', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (34, '校园文化节即将举行', '音乐、舞蹈、戏剧等多种形式的节目精彩呈现。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '杨帆', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 128, '校园活动', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (35, '我校师生在省级比赛获奖', '斩获编程、英语演讲等多项省级大奖。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '苏梅', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 139, '竞赛获奖', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (36, '“三下乡”活动纪实', '暑期社会实践团队奔赴各地服务基层群众。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '魏强', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 52, '社会实践', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (37, '学院发布暑期实习安排', '2025年暑期实习计划发布，覆盖多个院系。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '王蕾', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 101, '就业动态', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (38, '学生组织纳新启动', '社团、学生会等组织面向全校开展纳新。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '韩冰', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 61, '学生工作', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (39, '校长新年致辞发布', '鼓舞人心的新年寄语，展望美好未来。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '校办', 3, '2025-06-30 10:40:12', '2025-07-01 09:21:16', 202, '校园新闻', 0);
INSERT INTO `tb_news` (`news_id`, `title`, `content`, `image_urls`, `author_name`, `publisher_id`, `publish_time`, `update_time`, `read_count`, `tags`, `is_deleted`) VALUES (40, '计算机课程改革实施', '新学期起采用项目驱动+小组协作教学模式。', 'uploads/news/8c12eeae-8848-4d75-85f3-676544ee3349.jpg', '陈刚', 3, '2025-06-30 10:40:12', '2025-06-30 10:40:12', 75, '教学改革', 0);
COMMIT;

-- ----------------------------
-- Table structure for tb_news_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_news_comment`;
CREATE TABLE `tb_news_comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `news_id` int NOT NULL COMMENT '对应的新闻ID',
  `user_id` int NOT NULL COMMENT '评论人ID（tb_person_info.person_id）',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标志',
  PRIMARY KEY (`comment_id`) USING BTREE,
  KEY `idx_news` (`news_id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  CONSTRAINT `fk_comment_news` FOREIGN KEY (`news_id`) REFERENCES `tb_news` (`news_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='新闻评论表';

-- ----------------------------
-- Records of tb_news_comment
-- ----------------------------
BEGIN;
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (1, 21, 3, '非常精彩的活动，期待下一次！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (2, 22, 4, '内容很有启发性，值得一读。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (3, 23, 5, '希望多举办这种讲座！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (4, 24, 6, '就业率真不错，学校给力！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (5, 25, 3, '新生教育内容很实用。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (6, 26, 4, '图书馆越来越漂亮了！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (7, 27, 5, 'AI实验室我去参观过，很先进。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (8, 28, 6, '创业大赛真的激励人心。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (9, 29, 3, '展览作品很有创意！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (10, 30, 4, '校友分享干货满满。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (11, 31, 5, '企业参观让我学到很多。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (12, 32, 6, '简历培训真的帮大忙了。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (13, 33, 3, '心理健康活动非常有意义。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (14, 34, 4, '文化节太精彩了！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (15, 35, 5, '为获奖同学点赞！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (16, 36, 6, '三下乡活动让人感动。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (17, 37, 3, '我已经报名暑期实习了！', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (18, 38, 4, '学生组织太热情了，欢迎加入。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (19, 39, 5, '校长讲话很有力量。', '2025-06-30 11:28:51', 0);
INSERT INTO `tb_news_comment` (`comment_id`, `news_id`, `user_id`, `content`, `create_time`, `is_deleted`) VALUES (20, 40, 6, '课程改革方向不错，希望能参与更多项目。', '2025-06-30 11:28:51', 0);
COMMIT;

-- ----------------------------
-- Table structure for tb_organization_num
-- ----------------------------
DROP TABLE IF EXISTS `tb_organization_num`;
CREATE TABLE `tb_organization_num` (
  `num_id` int NOT NULL AUTO_INCREMENT,
  `sum` int NOT NULL,
  `class_id` int DEFAULT NULL,
  `college_id` int DEFAULT NULL,
  `specialty_id` int DEFAULT NULL,
  PRIMARY KEY (`num_id`) USING BTREE,
  KEY `tb_organization_num_tb_class_grade` (`class_id`) USING BTREE,
  KEY `tb_organization_num_tb_college` (`college_id`) USING BTREE,
  KEY `tb_organization_num_tb_specialty` (`specialty_id`) USING BTREE,
  CONSTRAINT `tb_organization_num_tb_class_grade` FOREIGN KEY (`class_id`) REFERENCES `tb_class_grade` (`class_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_organization_num_tb_college` FOREIGN KEY (`college_id`) REFERENCES `tb_college` (`college_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_organization_num_tb_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `tb_specialty` (`specialty_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_organization_num
-- ----------------------------
BEGIN;
INSERT INTO `tb_organization_num` (`num_id`, `sum`, `class_id`, `college_id`, `specialty_id`) VALUES (1, 40, 1, 1, 1);
INSERT INTO `tb_organization_num` (`num_id`, `sum`, `class_id`, `college_id`, `specialty_id`) VALUES (2, 35, 2, 2, 2);
COMMIT;

-- ----------------------------
-- Table structure for tb_person_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_person_info`;
CREATE TABLE `tb_person_info` (
  `person_id` int NOT NULL AUTO_INCREMENT,
  `enable_Status` int DEFAULT '0',
  `person_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `college_id` int DEFAULT NULL,
  PRIMARY KEY (`person_id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_person_info
-- ----------------------------
BEGIN;
INSERT INTO `tb_person_info` (`person_id`, `enable_Status`, `person_name`, `create_time`, `password`, `username`, `college_id`) VALUES (3, 3, '超级管理员', '2020-03-06 19:27:50', 'admin', 'admin123456', NULL);
INSERT INTO `tb_person_info` (`person_id`, `enable_Status`, `person_name`, `create_time`, `password`, `username`, `college_id`) VALUES (4, 1, '测试hr', '2025-06-29 13:24:56', '123', 'HR', 1);
INSERT INTO `tb_person_info` (`person_id`, `enable_Status`, `person_name`, `create_time`, `password`, `username`, `college_id`) VALUES (5, 0, '小明', '2025-06-30 11:27:19', '123', '123', 1);
INSERT INTO `tb_person_info` (`person_id`, `enable_Status`, `person_name`, `create_time`, `password`, `username`, `college_id`) VALUES (6, 0, '小美', '2025-06-30 11:27:52', '123', '1234', 1);
COMMIT;

-- ----------------------------
-- Table structure for tb_presentation
-- ----------------------------
DROP TABLE IF EXISTS `tb_presentation`;
CREATE TABLE `tb_presentation` (
  `presentation_id` int NOT NULL AUTO_INCREMENT COMMENT '宣讲会ID',
  `company_id` int NOT NULL COMMENT '公司人员ID，对应tb_person_info.person_id，enable_status=2',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '宣讲会主题/标题',
  `location` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '举办地点（如教室编号或地址）',
  `capacity` int NOT NULL COMMENT '场地可容纳人数',
  `start_time` datetime NOT NULL COMMENT '宣讲会开始时间',
  `end_time` datetime NOT NULL COMMENT '宣讲会结束时间',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '宣讲会简介/说明',
  `status` tinyint DEFAULT '0' COMMENT '状态（0=待审批，1=已通过，2=已拒绝）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '备注说明',
  `signup_count` int DEFAULT '0' COMMENT '当前报名人数',
  PRIMARY KEY (`presentation_id` DESC) USING BTREE,
  KEY `idx_company_id` (`company_id`) USING BTREE,
  CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_company_info` (`company_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='宣讲会申请记录表';

-- ----------------------------
-- Records of tb_presentation
-- ----------------------------
BEGIN;
INSERT INTO `tb_presentation` (`presentation_id`, `company_id`, `title`, `location`, `capacity`, `start_time`, `end_time`, `description`, `status`, `create_time`, `update_time`, `remark`, `signup_count`) VALUES (3, 1, '2025届春招宣讲会', 'B203', 100, '2025-07-05 10:00:00', '2025-07-05 12:00:00', '主要介绍公司招聘需求、岗位信息', 1, '2025-07-01 17:35:04', '2025-07-01 18:07:26', '审核通过', 1);
COMMIT;

-- ----------------------------
-- Table structure for tb_presentation_signup
-- ----------------------------
DROP TABLE IF EXISTS `tb_presentation_signup`;
CREATE TABLE `tb_presentation_signup` (
  `signup_id` int NOT NULL AUTO_INCREMENT COMMENT '报名记录ID',
  `presentation_id` int NOT NULL COMMENT '宣讲会ID',
  `student_id` int NOT NULL COMMENT '报名学生ID（tb_person_info.person_id, enable_status=0）',
  `signup_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  PRIMARY KEY (`signup_id`) USING BTREE,
  UNIQUE KEY `uniq_pres_student` (`presentation_id`,`student_id`) USING BTREE,
  KEY `fk_signup_student` (`student_id`) USING BTREE,
  CONSTRAINT `fk_signup_presentation` FOREIGN KEY (`presentation_id`) REFERENCES `tb_presentation` (`presentation_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_signup_student` FOREIGN KEY (`student_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='宣讲会报名表';

-- ----------------------------
-- Records of tb_presentation_signup
-- ----------------------------
BEGIN;
INSERT INTO `tb_presentation_signup` (`signup_id`, `presentation_id`, `student_id`, `signup_time`) VALUES (2, 3, 5, '2025-07-01 18:07:26');
COMMIT;

-- ----------------------------
-- Table structure for tb_private_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_private_message`;
CREATE TABLE `tb_private_message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` int NOT NULL COMMENT '发送者ID（tb_person_info.person_id）',
  `receiver_id` int NOT NULL COMMENT '接收者ID（tb_person_info.person_id）',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '消息内容',
  `send_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读（0=未读，1=已读）',
  `is_deleted_sender` tinyint DEFAULT '0' COMMENT '发送方是否删除（0=否，1=是）',
  `is_deleted_receiver` tinyint DEFAULT '0' COMMENT '接收方是否删除（0=否，1=是）',
  `message_type` tinyint DEFAULT '0' COMMENT '消息类型（0=文本，1=图片，2=文件）',
  `file_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '附件或图片的URL',
  PRIMARY KEY (`message_id`) USING BTREE,
  KEY `idx_sender` (`sender_id`) USING BTREE,
  KEY `idx_receiver` (`receiver_id`) USING BTREE,
  CONSTRAINT `fk_private_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_private_sender` FOREIGN KEY (`sender_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='私聊消息表';

-- ----------------------------
-- Records of tb_private_message
-- ----------------------------
BEGIN;
INSERT INTO `tb_private_message` (`message_id`, `sender_id`, `receiver_id`, `content`, `send_time`, `is_read`, `is_deleted_sender`, `is_deleted_receiver`, `message_type`, `file_url`) VALUES (2, 3, 4, 'API.docx', '2025-06-29 14:55:09', 0, 1, 0, 2, NULL);
INSERT INTO `tb_private_message` (`message_id`, `sender_id`, `receiver_id`, `content`, `send_time`, `is_read`, `is_deleted_sender`, `is_deleted_receiver`, `message_type`, `file_url`) VALUES (3, 3, 4, 'API.docx', '2025-06-29 15:01:50', 0, 0, 0, 2, '/uploads/messages/156b82b6-5f70-443c-992e-cd69f19f2dd3.docx');
INSERT INTO `tb_private_message` (`message_id`, `sender_id`, `receiver_id`, `content`, `send_time`, `is_read`, `is_deleted_sender`, `is_deleted_receiver`, `message_type`, `file_url`) VALUES (5, 3, 4, '你好，这是测试消息', '2025-07-01 16:23:26', 0, 0, 0, 0, NULL);
INSERT INTO `tb_private_message` (`message_id`, `sender_id`, `receiver_id`, `content`, `send_time`, `is_read`, `is_deleted_sender`, `is_deleted_receiver`, `message_type`, `file_url`) VALUES (6, 3, 4, 'API.docx', '2025-07-01 17:11:25', 0, 0, 0, 2, '/uploads/messages/34e2b75d-a5b2-44fd-a2e8-e32ff2b59f9e.docx');
COMMIT;

-- ----------------------------
-- Table structure for tb_resume
-- ----------------------------
DROP TABLE IF EXISTS `tb_resume`;
CREATE TABLE `tb_resume` (
  `resume_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '关联统一认证用户ID',
  `resume_name` varchar(100) NOT NULL COMMENT '简历名称',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认简历',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-草稿，1-发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`resume_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简历基本信息表';

-- ----------------------------
-- Records of tb_resume
-- ----------------------------
BEGIN;
INSERT INTO `tb_resume` (`resume_id`, `user_id`, `resume_name`, `is_default`, `status`, `create_time`, `update_time`) VALUES (1, 1, '测试简历', 0, 1, '2025-06-30 10:47:28', '2025-06-30 10:47:28');
INSERT INTO `tb_resume` (`resume_id`, `user_id`, `resume_name`, `is_default`, `status`, `create_time`, `update_time`) VALUES (9, 1, '我的简历', 0, 1, NULL, '2025-07-02 11:11:57');
INSERT INTO `tb_resume` (`resume_id`, `user_id`, `resume_name`, `is_default`, `status`, `create_time`, `update_time`) VALUES (10, 1, '我的简历', 0, 1, NULL, '2025-07-02 13:23:00');
INSERT INTO `tb_resume` (`resume_id`, `user_id`, `resume_name`, `is_default`, `status`, `create_time`, `update_time`) VALUES (11, 1, '我的简历', 1, 1, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for tb_resume_delivery
-- ----------------------------
DROP TABLE IF EXISTS `tb_resume_delivery`;
CREATE TABLE `tb_resume_delivery` (
  `delivery_id` int NOT NULL AUTO_INCREMENT,
  `resume_id` int NOT NULL COMMENT '简历ID',
  `job_id` int NOT NULL COMMENT '岗位ID',
  `company_id` int NOT NULL COMMENT '企业ID',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待查看，1-已查看，2-邀请面试，3-不合适',
  `delivery_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `feedback` text COMMENT '企业反馈',
  PRIMARY KEY (`delivery_id`),
  KEY `idx_resume_id` (`resume_id`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简历投递记录表';

-- ----------------------------
-- Records of tb_resume_delivery
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for tb_resume_detail
-- ----------------------------
DROP TABLE IF EXISTS `tb_resume_detail`;
CREATE TABLE `tb_resume_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `resume_id` int NOT NULL COMMENT '关联简历ID',
  `basic_info` json DEFAULT NULL COMMENT '基本信息(姓名、性别、出生日期等)',
  `education` json DEFAULT NULL COMMENT '教育经历',
  `work_experience` json DEFAULT NULL COMMENT '工作经历',
  `project_experience` json DEFAULT NULL COMMENT '项目经历',
  `skills` json DEFAULT NULL COMMENT '技能特长',
  `certificates` json DEFAULT NULL COMMENT '证书',
  `self_evaluation` text COMMENT '自我评价',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '附件URL',
  PRIMARY KEY (`detail_id`),
  UNIQUE KEY `uk_resume_id` (`resume_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简历详细内容表';

-- ----------------------------
-- Records of tb_resume_detail
-- ----------------------------
BEGIN;
INSERT INTO `tb_resume_detail` (`detail_id`, `resume_id`, `basic_info`, `education`, `work_experience`, `project_experience`, `skills`, `certificates`, `self_evaluation`, `attachment_url`) VALUES (1, 9, NULL, '[{\"end\": \"2022-06\", \"begin\": \"2018-09\", \"major\": \"计算机科学\", \"university\": \"某大学\"}]', '[{\"end\": \"2024-06\", \"begin\": \"2022-07\", \"company\": \"某公司\", \"position\": \"软件工程师\"}]', '[{\"end\": \"2023-06\", \"name\": \"企业人才管理系统\", \"role\": \"开发者\", \"begin\": \"2023-01\", \"description\": \"负责系统开发\"}]', '[\"Java\", \"Spring Boot\", \"React\"]', '[{\"name\": \"校级编程大赛一等奖\", \"time\": \"2020-05\"}]', '积极主动，善于团队合作', NULL);
INSERT INTO `tb_resume_detail` (`detail_id`, `resume_id`, `basic_info`, `education`, `work_experience`, `project_experience`, `skills`, `certificates`, `self_evaluation`, `attachment_url`) VALUES (2, 10, NULL, '[{\"end\": \"2022-06\", \"begin\": \"2018-09\", \"major\": \"计算机科学\", \"university\": \"中南大学\"}]', '[{\"end\": \"2024-06\", \"begin\": \"2022-07\", \"company\": \"科技公司\", \"position\": \"软件工程师\"}]', '[{\"end\": \"2023-06\", \"name\": \"企业人才管理系统\", \"role\": \"开发者\", \"begin\": \"2023-01\", \"description\": \"负责系统开发\"}]', '[\"Java\", \"Spring Boot\", \"React\"]', '[{\"name\": \"校级编程大赛一等奖\", \"time\": \"2020-05\"}]', '积极主动，善于团队合作', NULL);
INSERT INTO `tb_resume_detail` (`detail_id`, `resume_id`, `basic_info`, `education`, `work_experience`, `project_experience`, `skills`, `certificates`, `self_evaluation`, `attachment_url`) VALUES (3, 11, NULL, '[{\"end\": \"2022-06\", \"begin\": \"2018-09\", \"major\": \"计算机科学\", \"university\": \"中南大学\"}]', '[{\"end\": \"2024-06\", \"begin\": \"2022-07\", \"company\": \"科技公司\", \"position\": \"软件工程师\"}]', '[{\"end\": \"2023-06\", \"name\": \"企业人才管理系统\", \"role\": \"开发者\", \"begin\": \"2023-01\", \"description\": \"负责系统开发\"}]', '[\"Java\", \"Spring Boot\", \"React\"]', '[{\"name\": \"校级编程大赛一等奖\", \"time\": \"2020-05\"}]', '积极主动，善于团队合作', NULL);
COMMIT;

-- ----------------------------
-- Table structure for tb_specialty
-- ----------------------------
DROP TABLE IF EXISTS `tb_specialty`;
CREATE TABLE `tb_specialty` (
  `specialty_id` int NOT NULL AUTO_INCREMENT,
  `specialty_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `college_id` int NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`specialty_id`) USING BTREE,
  KEY `tb_specialty_tb_college` (`college_id`) USING BTREE,
  CONSTRAINT `tb_specialty_tb_college` FOREIGN KEY (`college_id`) REFERENCES `tb_college` (`college_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_specialty
-- ----------------------------
BEGIN;
INSERT INTO `tb_specialty` (`specialty_id`, `specialty_name`, `college_id`, `create_time`) VALUES (1, '软件工程', 1, '2025-06-25 22:22:23');
INSERT INTO `tb_specialty` (`specialty_id`, `specialty_name`, `college_id`, `create_time`) VALUES (2, '信息管理', 2, '2025-06-25 22:22:23');
COMMIT;

-- ----------------------------
-- Table structure for tb_unit_kind
-- ----------------------------
DROP TABLE IF EXISTS `tb_unit_kind`;
CREATE TABLE `tb_unit_kind` (
  `unit_id` int NOT NULL AUTO_INCREMENT,
  `unit_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`unit_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tb_unit_kind
-- ----------------------------
BEGIN;
INSERT INTO `tb_unit_kind` (`unit_id`, `unit_name`, `create_time`) VALUES (1, '国企', '2020-03-06 13:56:23');
INSERT INTO `tb_unit_kind` (`unit_id`, `unit_name`, `create_time`) VALUES (2, '私企', '2020-03-06 13:56:29');
INSERT INTO `tb_unit_kind` (`unit_id`, `unit_name`, `create_time`) VALUES (3, '公务员', '2020-03-06 13:56:36');
INSERT INTO `tb_unit_kind` (`unit_id`, `unit_name`, `create_time`) VALUES (4, '事业单位', '2020-03-06 13:56:43');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
