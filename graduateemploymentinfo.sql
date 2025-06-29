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

 Date: 29/06/2025 10:24:21
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
  `user_type` tinyint(1) DEFAULT NULL COMMENT '用户类型：1-学生，2-企业HR，3-就业指导老师，4-系统管理员',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `source_system` varchar(20) DEFAULT NULL COMMENT '用户来源系统',
  `source_id` bigint DEFAULT NULL COMMENT '源系统用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一认证用户表';

-- ----------------------------
-- Records of employment_user
-- ----------------------------
BEGIN;
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
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
BEGIN;
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
  PRIMARY KEY (`company_id`) USING BTREE,
  KEY `area_id` (`area_id`) USING BTREE,
  CONSTRAINT `fk_company_area` FOREIGN KEY (`area_id`) REFERENCES `tb_area` (`area_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='参会企业信息表';

-- ----------------------------
-- Records of tb_company_info
-- ----------------------------
BEGIN;
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
  PRIMARY KEY (`job_fair_id`) USING BTREE,
  KEY `organizer_id` (`organizer_id`) USING BTREE,
  CONSTRAINT `fk_job_fair_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='招聘会信息表';

-- ----------------------------
-- Records of tb_job_fair
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for tb_job_fair_company
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_fair_company`;
CREATE TABLE `tb_job_fair_company` (
  `job_fair_id` int NOT NULL COMMENT '招聘会ID',
  `company_id` int NOT NULL COMMENT '企业ID',
  `booth_location` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '展位位置',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';

-- ----------------------------
-- Records of tb_job_position
-- ----------------------------
BEGIN;
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
INSERT INTO `tb_person_info` (`person_id`, `enable_Status`, `person_name`, `create_time`, `password`, `username`, `college_id`) VALUES (3, 2, '超级管理员', '2020-03-06 19:27:50', 'admin1', 'admin', NULL);
INSERT INTO `tb_person_info` (`person_id`, `enable_Status`, `person_name`, `create_time`, `password`, `username`, `college_id`) VALUES (32, 2, '测试', '2025-06-28 10:42:37', '123', '2', NULL);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简历基本信息表';

-- ----------------------------
-- Records of tb_resume
-- ----------------------------
BEGIN;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简历详细内容表';

-- ----------------------------
-- Records of tb_resume_detail
-- ----------------------------
BEGIN;
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
