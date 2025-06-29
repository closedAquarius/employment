/*
 Navicat Premium Data Transfer

 Source Server         : web
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : graduateemploymentinfo2

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 29/06/2025 14:21:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `person_id` int NOT NULL,
  `enable_status` int NULL DEFAULT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `operation_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `target` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `operation_time` datetime NULL DEFAULT NULL,
  `success` tinyint(1) NULL DEFAULT NULL,
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for tb_area
-- ----------------------------
DROP TABLE IF EXISTS `tb_area`;
CREATE TABLE `tb_area`  (
  `area_id` int NOT NULL AUTO_INCREMENT,
  `area_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `parent_id` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`area_id`) USING BTREE,
  INDEX `tb_area_parent`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `tb_area_parent` FOREIGN KEY (`parent_id`) REFERENCES `tb_area` (`area_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_area
-- ----------------------------
INSERT INTO `tb_area` VALUES (4, '四川', NULL, '2020-03-06 13:39:59');
INSERT INTO `tb_area` VALUES (5, '重庆', NULL, '2020-03-06 13:40:14');
INSERT INTO `tb_area` VALUES (6, '成都', 4, '2020-03-06 13:46:34');
INSERT INTO `tb_area` VALUES (8, '河北', NULL, '2020-03-06 23:35:56');
INSERT INTO `tb_area` VALUES (9, '石家庄', 8, '2020-03-06 23:36:40');
INSERT INTO `tb_area` VALUES (10, '广东', NULL, '2020-03-06 23:36:51');
INSERT INTO `tb_area` VALUES (11, '广州', 10, '2020-03-06 23:36:59');
INSERT INTO `tb_area` VALUES (12, '浙江', NULL, '2020-03-06 23:37:21');
INSERT INTO `tb_area` VALUES (13, '杭州', 12, '2020-03-06 23:37:38');
INSERT INTO `tb_area` VALUES (14, '湖北', NULL, '2020-03-06 23:38:06');
INSERT INTO `tb_area` VALUES (15, '武汉', 14, '2020-03-06 23:38:17');
INSERT INTO `tb_area` VALUES (16, '江苏', NULL, '2020-03-06 23:38:33');
INSERT INTO `tb_area` VALUES (17, '南京', 16, '2020-03-06 23:38:45');
INSERT INTO `tb_area` VALUES (18, '辽宁', NULL, '2020-03-06 23:39:21');
INSERT INTO `tb_area` VALUES (19, '沈阳', 18, '2020-03-06 23:39:40');
INSERT INTO `tb_area` VALUES (20, '湖南', NULL, '2020-03-06 23:39:55');
INSERT INTO `tb_area` VALUES (21, '长沙', 20, '2020-03-06 23:40:03');
INSERT INTO `tb_area` VALUES (22, '河南', NULL, '2020-03-06 23:40:22');
INSERT INTO `tb_area` VALUES (23, '郑州', 22, '2020-03-06 23:40:38');
INSERT INTO `tb_area` VALUES (24, '山东', NULL, '2020-03-06 23:40:48');
INSERT INTO `tb_area` VALUES (25, '济南', 24, '2020-03-06 23:41:01');
INSERT INTO `tb_area` VALUES (26, '黑龙江', NULL, '2020-03-06 23:41:18');
INSERT INTO `tb_area` VALUES (27, '哈尔滨', 26, '2020-03-06 23:41:30');
INSERT INTO `tb_area` VALUES (28, '吉林', NULL, '2020-03-06 23:41:39');
INSERT INTO `tb_area` VALUES (29, '长春', 28, '2020-03-06 23:41:51');
INSERT INTO `tb_area` VALUES (30, '陕西', NULL, '2020-03-06 23:42:04');
INSERT INTO `tb_area` VALUES (31, '西安', 30, '2020-03-06 23:42:28');
INSERT INTO `tb_area` VALUES (32, '福建', NULL, '2020-03-06 23:42:40');
INSERT INTO `tb_area` VALUES (33, '福州', 32, '2020-03-06 23:42:50');
INSERT INTO `tb_area` VALUES (34, '安徽', NULL, '2020-03-06 23:43:04');
INSERT INTO `tb_area` VALUES (35, '合肥', 34, '2020-03-06 23:43:12');
INSERT INTO `tb_area` VALUES (36, '江西', NULL, '2020-03-06 23:43:22');
INSERT INTO `tb_area` VALUES (37, '南昌', 36, '2020-03-06 23:43:38');
INSERT INTO `tb_area` VALUES (38, '云南', NULL, '2020-03-06 23:43:56');
INSERT INTO `tb_area` VALUES (39, '昆明', 38, '2020-03-06 23:44:10');
INSERT INTO `tb_area` VALUES (40, '内蒙古', NULL, '2020-03-06 23:44:22');
INSERT INTO `tb_area` VALUES (41, '呼和浩特', 40, '2020-03-06 23:44:50');
INSERT INTO `tb_area` VALUES (42, '广西', NULL, '2020-03-06 23:45:00');
INSERT INTO `tb_area` VALUES (43, '南宁', 42, '2020-03-06 23:45:15');
INSERT INTO `tb_area` VALUES (44, '山西', NULL, '2020-03-06 23:45:33');
INSERT INTO `tb_area` VALUES (45, '太原', 44, '2020-03-06 23:45:45');
INSERT INTO `tb_area` VALUES (46, '新疆', NULL, '2020-03-06 23:45:54');
INSERT INTO `tb_area` VALUES (47, '乌鲁木齐', 46, '2020-03-06 23:46:07');
INSERT INTO `tb_area` VALUES (48, '贵州', NULL, '2020-03-06 23:46:16');
INSERT INTO `tb_area` VALUES (49, '贵阳', 48, '2020-03-06 23:46:28');
INSERT INTO `tb_area` VALUES (50, '甘肃', NULL, '2020-03-06 23:46:38');
INSERT INTO `tb_area` VALUES (51, '兰州', 50, '2020-03-06 23:46:53');
INSERT INTO `tb_area` VALUES (52, '青海', NULL, '2020-03-06 23:47:04');
INSERT INTO `tb_area` VALUES (53, '西宁', 52, '2020-03-06 23:47:15');
INSERT INTO `tb_area` VALUES (54, '海南', NULL, '2020-03-06 23:47:27');
INSERT INTO `tb_area` VALUES (55, '海口', 54, '2020-03-06 23:47:39');
INSERT INTO `tb_area` VALUES (56, '宁夏', NULL, '2020-03-06 23:47:57');
INSERT INTO `tb_area` VALUES (57, '银川', 56, '2020-03-06 23:48:10');
INSERT INTO `tb_area` VALUES (58, '西藏', NULL, '2020-03-06 23:48:36');
INSERT INTO `tb_area` VALUES (59, '拉萨', 58, '2020-03-06 23:48:46');
INSERT INTO `tb_area` VALUES (60, '台湾', NULL, '2020-03-06 23:49:17');
INSERT INTO `tb_area` VALUES (61, '台北', 60, '2020-03-06 23:49:25');
INSERT INTO `tb_area` VALUES (62, '北京市', NULL, '2020-03-06 23:50:22');
INSERT INTO `tb_area` VALUES (63, '上海市', NULL, '2020-03-06 23:50:34');
INSERT INTO `tb_area` VALUES (64, '天津市', NULL, '2020-03-06 23:50:49');
INSERT INTO `tb_area` VALUES (65, '深圳', 10, '2020-03-06 23:52:25');
INSERT INTO `tb_area` VALUES (66, '香港', NULL, '2020-03-07 00:00:09');
INSERT INTO `tb_area` VALUES (67, '澳门', NULL, '2020-03-07 00:00:16');

-- ----------------------------
-- Table structure for tb_class_grade
-- ----------------------------
DROP TABLE IF EXISTS `tb_class_grade`;
CREATE TABLE `tb_class_grade`  (
  `class_id` int NOT NULL AUTO_INCREMENT,
  `class_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `specialty_id` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `admin_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`class_id`) USING BTREE,
  INDEX `tb_class_grade_tb_specialty`(`specialty_id` ASC) USING BTREE,
  INDEX `tb_class_grade_tb_person_info`(`admin_id` ASC) USING BTREE,
  CONSTRAINT `tb_class_grade_tb_person_info` FOREIGN KEY (`admin_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_class_grade_tb_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `tb_specialty` (`specialty_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_class_grade
-- ----------------------------
INSERT INTO `tb_class_grade` VALUES (1, '软件工程2016级1班', 1, '2025-06-25 22:22:23', 3);
INSERT INTO `tb_class_grade` VALUES (2, '信息管理2016级1班', 2, '2025-06-25 22:22:23', 3);

-- ----------------------------
-- Table structure for tb_college
-- ----------------------------
DROP TABLE IF EXISTS `tb_college`;
CREATE TABLE `tb_college`  (
  `college_id` int NOT NULL AUTO_INCREMENT,
  `college_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `admin_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`college_id`) USING BTREE,
  INDEX `tb_college_tb_person_info`(`admin_id` ASC) USING BTREE,
  CONSTRAINT `tb_college_tb_person_info` FOREIGN KEY (`admin_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_college
-- ----------------------------
INSERT INTO `tb_college` VALUES (1, '计算机学院', '2025-06-25 22:22:23', 3);
INSERT INTO `tb_college` VALUES (2, '管理学院', '2025-06-25 22:22:23', 3);

-- ----------------------------
-- Table structure for tb_company_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_company_info`;
CREATE TABLE `tb_company_info`  (
  `company_id` int NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `company_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '企业名称',
  `company_intro` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '企业简介',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '企业地址',
  `area_id` int NULL DEFAULT NULL COMMENT '所属地区',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `unit_id` int NULL DEFAULT NULL COMMENT '企业类型ID',
  PRIMARY KEY (`company_id`) USING BTREE,
  INDEX `area_id`(`area_id` ASC) USING BTREE,
  INDEX `unit_id`(`unit_id` ASC) USING BTREE,
  CONSTRAINT `fk_company_area` FOREIGN KEY (`area_id`) REFERENCES `tb_area` (`area_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_company_unit` FOREIGN KEY (`unit_id`) REFERENCES `tb_unit_kind` (`unit_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '参会企业信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_company_info
-- ----------------------------

-- ----------------------------
-- Table structure for tb_employment_information
-- ----------------------------
DROP TABLE IF EXISTS `tb_employment_information`;
CREATE TABLE `tb_employment_information`  (
  `information_id` int NOT NULL AUTO_INCREMENT,
  `student_num` int NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `class_id` int NOT NULL,
  `area_id` int NOT NULL,
  `unit_id` int NOT NULL,
  `salary` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `employment_way_id` int NOT NULL,
  `msg` varchar(10000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `gender` int NOT NULL,
  `college_id` int NOT NULL,
  `specialty_id` int NOT NULL,
  PRIMARY KEY (`information_id`) USING BTREE,
  UNIQUE INDEX `student_num_qurey`(`student_num` ASC) USING BTREE,
  INDEX `tb_employment_information_tb_class_grade`(`class_id` ASC) USING BTREE,
  INDEX `tb_employment_information_tb_area`(`area_id` ASC) USING BTREE,
  INDEX `tb_employment_information_tb_unit_kind`(`unit_id` ASC) USING BTREE,
  INDEX `tb_employment_information_tb_employment_way`(`employment_way_id` ASC) USING BTREE,
  INDEX `tb_employment_information_tb_college`(`college_id` ASC) USING BTREE,
  INDEX `tb_employment_information_tb_specialty`(`specialty_id` ASC) USING BTREE,
  CONSTRAINT `tb_employment_information_tb_area` FOREIGN KEY (`area_id`) REFERENCES `tb_area` (`area_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_class_grade` FOREIGN KEY (`class_id`) REFERENCES `tb_class_grade` (`class_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_college` FOREIGN KEY (`college_id`) REFERENCES `tb_college` (`college_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_employment_way` FOREIGN KEY (`employment_way_id`) REFERENCES `tb_employment_way` (`employment_way_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `tb_specialty` (`specialty_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_employment_information_tb_unit_kind` FOREIGN KEY (`unit_id`) REFERENCES `tb_unit_kind` (`unit_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1036 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_employment_information
-- ----------------------------
INSERT INTO `tb_employment_information` VALUES (1033, 20200101, '张三', 1, 6, 1, '8000', 2, '工作地点在成都', '2025-06-25 22:22:23', 1, 1, 1);
INSERT INTO `tb_employment_information` VALUES (1034, 20200102, '李四', 1, 9, 2, '7000', 1, '河北老家工作', '2025-06-25 22:22:23', 0, 1, 1);
INSERT INTO `tb_employment_information` VALUES (1035, 20200103, '王五', 2, 11, 3, '9000', 3, '公务员编制', '2025-06-25 22:22:23', 1, 2, 2);

-- ----------------------------
-- Table structure for tb_employment_way
-- ----------------------------
DROP TABLE IF EXISTS `tb_employment_way`;
CREATE TABLE `tb_employment_way`  (
  `employment_way_id` int NOT NULL AUTO_INCREMENT,
  `vay_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`employment_way_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_employment_way
-- ----------------------------
INSERT INTO `tb_employment_way` VALUES (1, '学校双选会', '2020-03-06 13:57:09');
INSERT INTO `tb_employment_way` VALUES (2, '互联网招聘平台', '2020-03-06 13:57:27');
INSERT INTO `tb_employment_way` VALUES (3, '个人寻找', '2020-03-06 13:57:45');
INSERT INTO `tb_employment_way` VALUES (4, '朋友推荐', '2020-03-06 13:57:59');

-- ----------------------------
-- Table structure for tb_job_fair
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_fair`;
CREATE TABLE `tb_job_fair`  (
  `job_fair_id` int NOT NULL AUTO_INCREMENT COMMENT '招聘会ID',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '招聘会主题',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '招聘会简介',
  `location` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '举办地点',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `organizer_id` int NULL DEFAULT NULL COMMENT '主办人ID（管理员或教师）',
  `booth_total` int NULL DEFAULT 0 COMMENT '可用展位总数',
  PRIMARY KEY (`job_fair_id`) USING BTREE,
  INDEX `organizer_id`(`organizer_id` ASC) USING BTREE,
  CONSTRAINT `fk_job_fair_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '招聘会信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_job_fair
-- ----------------------------
INSERT INTO `tb_job_fair` VALUES (1, '2025届校园招聘会', '本次招聘会汇聚优质企业，欢迎同学们积极参与。', '中南大学双创中心', '2025-06-10 09:00:00', '2025-06-11 16:00:00', '2025-06-29 09:04:21', 3, 50);

-- ----------------------------
-- Table structure for tb_job_fair_company
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_fair_company`;
CREATE TABLE `tb_job_fair_company`  (
  `job_fair_id` int NOT NULL COMMENT '招聘会ID',
  `company_id` int NOT NULL COMMENT '企业ID',
  `booth_location` int NULL DEFAULT NULL COMMENT '展位位置',
  `status` int NULL DEFAULT 0 COMMENT '申请状态：0-待审核，1-通过，2-拒绝',
  PRIMARY KEY (`job_fair_id`, `company_id`) USING BTREE,
  INDEX `fk_jfc_company`(`company_id` ASC) USING BTREE,
  CONSTRAINT `fk_jfc_company` FOREIGN KEY (`company_id`) REFERENCES `tb_company_info` (`company_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_jfc_job_fair` FOREIGN KEY (`job_fair_id`) REFERENCES `tb_job_fair` (`job_fair_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '招聘会-参会企业关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_job_fair_company
-- ----------------------------

-- ----------------------------
-- Table structure for tb_job_posting
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_posting`;
CREATE TABLE `tb_job_posting`  (
  `position_id` int NOT NULL,
  `position_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `city` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `salary` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `company_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `company_tags` json NULL,
  `education_requirement` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `experience_required` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `job_id` int NOT NULL,
  `recruit_number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `preferred_majors` json NULL,
  `update_date` datetime NULL DEFAULT NULL,
  `job_description` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `job_place` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`job_id`, `position_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_job_posting
-- ----------------------------
INSERT INTO `tb_job_posting` VALUES (1, '管培生-营销运营', '「泰州·泰兴市」', '8000-12000元', '济川药业集团有限公司', '[\"医药制造\", \"1000-9999人\", \"上市公司\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-05-16 08:00:00', '岗位职责:\n1、根据市场发展，起草营销政策。同时对政策制定后的达成效果进行分析和纠偏；\n2、对营销线重点工作计划进行跟踪（目标制定，事项达成进度跟进，结果考核）；\n3、会议管理 （会议预约、会议布置及各项准备工作）；\n4、协同事项：营销线重点事项进度跟踪及复盘、市场问题处理、产供销会议、绩效报告书事项。\n\n\n任职要求:\n1、本科及以上学历，医药、市场营销、管理类等相关专业；\n2、具备良好的沟通能力和人际交往能力，能够与他人建立良好的合作关系；\n3、具备项目管理的经验＆能力；\n4、熟练掌握Excel＆PPT等办公软件；\n5、具备较强的学习能力和适应能力；能够快速适应公司文化和岗位要求。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (2, '计算机信息类', '「徐州」', '面议', '徐州矿务集团有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招8人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：计算机科学与技术、电子信息工程、网络工程等', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (3, '环境科学岗', '「无锡·滨湖」', '面议', '中国船舶集团有限公司', '[\"船舶/航空/航天/火车制造\", \"10000人以上\", \"国企\"]', '本科', '经验不限', 373, '若干', '[\"环境科学与工程\"]', '2025-04-07 08:00:00', '备注：该岗位为我单位下属子公司招聘需求。\n岗位要求：\n1. 本科及以上学历，环境科学、环境工程或相关专业\n2. 具备良好的分析和解决问题的能力，能够独立处理技术难题。\n3. 有良好的沟通和团队协作能力，能够与跨部门团队有效合作。\n4. 具备较强的学习能力和适应能力，能够快速掌握新技术和新方法。', '山水东路');
INSERT INTO `tb_job_posting` VALUES (4, '管理培训生', '「上海·浦东」', '1.7-2.5万', '上港集团', '[\"货运/物流/仓储\", \"100-299人\", \"上市公司\"]', '本科', '无经验', 373, '招2人', '[\"信息暂无\"]', '2025-03-06 08:00:00', '岗位内容:\n1.参与公司经营管理、业务流程优化、科技创新项目推进等工作。\n2.参与企业管理规范和内部流程优化。\n培养方向:\n企业经营管理、专业技术管理、综合管理等高级管理人员。\n任职要求:\n1.优秀院校本科及以上学历，有出色的团队协作和执行力。\n2.出色的逻辑分析能力和数据驱动思维，对业务流程改进有敏锐的嗅觉。\n3.勤奋上进，具备较强的抗压能力和问题解决能力。\n工作地点：临港新片区洋山3期码头（公司提供上下班提供班车）\n薪酬福利：公司提供行业内一流的薪酬待遇，除了国家规定的五险一金以外，还提供补充公积金、企业年金（补充养老金）、补充医保、交通补贴、差旅补贴、员工体检、生日礼物、带薪休假、中超球迷协会等福利待遇及工会福利。公司员工符合条件者，还可以享受临港新片区人才落户、人才购房及租房等各类优惠政策。', '上海冠东国际集装箱码头有限公司');
INSERT INTO `tb_job_posting` VALUES (5, '机器人测试工程师', '「上海·普陀」', '8000-16000元·14薪', '上海电器科学研究所（集团）有限公司', '[\"专业技术服务\", \"1000-9999人\", \"国企\"]', '本科', '无经验', 373, '招10人', '[\"控制理论与控制工程, 电机与电力电子, 电子信息工程, 机器人工程, 计算机科学与技术, 软件工程\"]', '2025-04-10 08:00:00', '机器人功能测试、数据采集工作', '上海电器科学研究院(武宁路)');
INSERT INTO `tb_job_posting` VALUES (6, '管培生-人力资源', '「上海」', '面议', '济川药业集团有限公司', '[\"医药制造\", \"1000-9999人\", \"上市公司\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-04-11 08:00:00', '岗位职责:\n1、人力资源规划与策略：协助为公司制定与战略目标相匹配的人力资源策略和规划；\n2、招聘：负责应届毕业生招聘工作；协助成熟型人才的招聘；协助招聘制度与流程的优化执行；\n3、HRBP：协助业务中心团队绩效管理、员工关系管理及部分人才项目落地；\n4、薪酬与福利管理：参与制定薪酬福利体系及相关政策，完善和优化薪酬福利体系；\n5、培训与发展：负责社招NEO培训的组织实施，并跟进培训效果；协助营销培训的组织、测评、跟进；协助人才发展项目的开展与跟踪；负责部分课程的开发与授课。\n\n\n任职要求:\n1、本科及以上学历，人力资源管理、工商管理、心理学等相关专业优先；\n2、较强的学习能力和责任心，工作积极主动，思维敏捷，逻辑思维能力出色；\n3、熟练使用excel、PowerPoint等办公软件，具备较强的语言表达、数据分析、公文撰写等能力。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (7, '行政助理', '「徐州」', '面议', '徐州国力电力设备有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：不限专业', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (8, '环境工程', '「泰州」', '5000-10000元', '江苏杭富环保科技有限公司', '[\"环保\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '招6人', '[\"材料类\"]', '2025-04-24 08:00:00', '经验要求不限，专业要求：地质类,环境科学与工程类,经济学类,生物科学类', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (9, '管理培训生', '「上海·浦东」', '1.7-2.5万', '上港集团', '[\"货运/物流/仓储\", \"100-299人\", \"上市公司\"]', '本科', '无经验', 373, '招2人', '[\"信息暂无\"]', '2025-04-14 08:00:00', '岗位内容:\n1.参与公司经营管理、业务流程优化、科技创新项目推进等工作。\n2.参与企业管理规范和内部流程优化。\n培养方向:\n企业经营管理、专业技术管理、综合管理等高级管理人员。\n任职要求:\n1.优秀院校本科及以上学历，有出色的团队协作和执行力。\n2.出色的逻辑分析能力和数据驱动思维，对业务流程改进有敏锐的嗅觉。\n3.勤奋上进，具备较强的抗压能力和问题解决能力。', '上海冠东国际集装箱码头有限公司');
INSERT INTO `tb_job_posting` VALUES (10, '医院质量管理', '「丽水」', '1-1.2万', '龙泉市人民医院（浙江大学附属第二医院龙泉分院）', '[\"社团/组织/社会保障\", \"100-299人\", \"其它\"]', '本科', '经验不限', 373, '招1人', '[\"公共卫生管理\"]', '2025-03-20 08:00:00', '35周岁及以下（1988年3月26日以后出生）,本科及以上学历，专业要求：公共卫生管理、卫生事业管理、医学信息学、健康服务与管理、健康管理', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (11, '管培生-总裁办', '「上海」', '面议', '济川药业集团有限公司', '[\"医药制造\", \"1000-9999人\", \"上市公司\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-05-14 08:00:00', '岗位职责:\n1、协助跟进集团级别的项目、流程；\n2、协助开展集团体系化建设工作，完善与优化管理标准，提升管理效能；\n3、协助组织集团各类管理专项工作，确保管理专项任务有效落地；\n4、协助组织召开集团重大运营/管理会议，输出会议纪要，并督办会议任务达成；\n5、协助编制各类文字方案与材料，包括策划方案、政府呈文、公司报告、发言材料等；\n6、上级安排的其它工作。\n\n\n任职要求:\n1、本科及以上学历，药学、医学等相关专业；\n2、熟练操作Office软件；\n3、具有较好的逻辑思维、文字撰写与组织协调能力；\n4、具备一定的抗压能力，能够适应高强度的工作环境；\n5、具有较强的学习能力与沟通能力；\n6、具有医药公司实习经历优先。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (12, '城市经理', '「徐州」', '面议', '维维食品饮料股份有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：不限，市场营销等相关专业优先', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (13, '金融管培生', '「上海·徐汇」', '1-1.5万·15薪', '上海孚厘科技有限公司', '[\"互联网\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '若干', '[\"金融学, 经济学, 财务管理, 市场营销\"]', '2025-05-13 08:00:00', '该岗位面向2025及2026届应届毕业生，旨在为公司一线营销团队培养管理人才。\n\n岗位职责：\n1.第一年需拓展客户资源，建立和维护目标客户群的业务关系，开展客户资信调查及资产保全工作；\n2.与相关业务方沟通，负责跟进内外部业务相关问题，确保项目高效高质量上线；\n3.轮岗过程中关注业务痛点及行业动态，参与相关行业规则制定和产品规划，主动优化打磨产品，不断提升产品价值上限；\n4.第一年销售轮岗后，根据履职表现及公司实际业务需要，结合个人发展意愿与擅长方向可转岗中后台职能部门（风控/运营/产品/数科等）。\n\n任职要求：\n1.本科及以上学历，金融或财务相关专业，仅限应届生或往届生（无金融从业背景）；\n2.学习力强，积极向上，希望和一群文化价值观正确、正直、进取的人一起奋斗；\n3.喜欢与人交流，有强烈成就动机，敢于挑战高薪，认可付出等于收获的理念；\n4.性格坚韧，战斗力超强，具备在困难、挫折、艰苦的逆境中生存的能力。', '星扬西岸中心2709-2712');
INSERT INTO `tb_job_posting` VALUES (14, '投资管理', '「徐州」', '面议', '徐州云盛建设发展投资集团有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：金融学、投资学、经济学、项目管理、财务管理等相关专业。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (15, 'QC检验员', '「徐州」', '面议', '江苏景泽生物制药有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招4人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：药学、食品药品、生物类相关专业', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (16, '助理工程师', '「徐州」', '面议', '强茂半导体（徐州有限公司）', '[\"通用设备制造\", \"500-999人\", \"港澳台公司\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：电子信息专业', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (17, '数据分析师', '「徐州」', '面议', '非凸智能科技（徐州）有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：数学、统计、大数据、财管、金融等理科专业', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (18, '有机合成研发员', '「泰州」', '7000-14000元', '江苏广域化学有限公司', '[\"化学原料/化学制品\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '招6人', '[\"材料类\"]', '2025-04-24 08:00:00', '经验要求不限，专业要求：化学相关专业', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (19, '理赔类岗位', '「徐州」', '面议', '中国人民财产保险股份有限公司徐州市分公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招3人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：车辆工程、交通运输、医学、法律、保险、金融、建筑、化工、机械、工程等相关专业。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (20, '管理培训生', '「南通·通州」', '7000-14000元·14薪', '海星股份', '[\"电子/半导体/集成电路\", \"500-999人\", \"上市公司\"]', '本科', '经验不限', 373, '若干', '[\"信息暂无\"]', '2025-06-05 08:00:00', '围绕公司人才梯队培养总体要求，学习、实践、成长，成为公司骨干。\n1、完成轮岗学习和相关工作任务；\n2、完成阶段性安排的项目工作；\n3、过程中明确定位、快速成长。\n岗位方向包括：投资、销售、供应链管理、生产管理、人资及行政管理', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (21, '2025届虹途生—设备管培生(J14994)', '「扬州·仪征市」', '面议', '东方雨虹', '[\"建材\", \"10000人以上\", \"上市公司\"]', '本科', '无经验', 373, '招5人', '[\"信息暂无\"]', '2025-03-26 08:00:00', '工作职责：\n1、全面管理工厂（聚合物乳液、胶粉车间）水、电、气等能耗管理，维护设备稳定运行，执行公司相关制度；\n2、负责设备台账管理，掌握全部电气运行情况；\n3、推进工厂年、季、月检修计划、编制设备大修计划；\n4、建立备品备件安全库存，合理控制备品所占用资金；\n5、认真抓好电气运行情况，出现故障及时维修，制定防范措施；\n6、负责新设备电气线路的安装、程序调试；\n7、组织设备部人员进行电气培训；\n8、上级领导交办的其他工作。\n任职资格：\n1、统招本科及以上学历，电气自动化、机械、电仪类相关专业；\n2、吃苦耐劳，擅于发现并分析解决问题，刻苦钻研，具备创新意识。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (22, '助理工程师', '「徐州」', '面议', '汉御微传感器（江苏）有限公司', '[\"通用设备制造\", \"500-999人\", \"其它\"]', '本科', '无经验', 373, '招1人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：电子科学与技术，电子信息，计算机应用、电气自动化、微电子、集成电路等', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (23, '美术老师', '「徐州」', '面议', '沛县汉城文昌学校', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招14人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：美术老师', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (24, '山姆门店管培生-杭州', '「杭州」', '面议', '沃尔玛中国', '[\"零售/批发\", \"10000人以上\", \"外商独资\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2025-04-29 08:00:00', '● 管理培训生项目简介：\n本项目是为了培养山姆会员店的门店未来管理者而设立，项目为期2-3年，管培生将通过轮岗、跨部门学习体验、系统化领导力及岗位培训，参与制定销售策略、落实门店运营管理、业绩管理等业务，快速成长为山姆会员店的门店副总经理。\n\n● 岗位职责：\n1. 零售新星-岗位基础业务培训：在门店各部门进行轮岗学习，熟悉一线业务，了解零售运作机制，熟习并掌握部门主管岗位所需知识和技能。熟悉公司各项合法合规要求。\n2. 部门操盘手-部门主管岗位工作：\n(1) 负责部门业务管理，制定和落实工作计划，带领团队完成各项指标，如销售预算等。\n(2) 组织和制定人员培养发展计划，有效调配人力资源，提升部门效率，同时培养有发展潜力的员工。\n(3) 在部门内营造公平透明的沟通氛围，实践公仆领导，不断提高员工士气，实施和监督人员发展培训计划，有计划性地激励和提高员工的专业水平，建立高效团队。\n(4) 遵守并培训团队遵守政策及法律法规，推动落实各项工作标准和程序，确保达到门店运营的安全标准。\n3. 储备领航人-门店见习副总：系统学习并实践门店副总经理的职责及工作内容。\n4. 分区领航人-门店副总：通过测评将成为门店副总经理，负责门店分区即多个部门的业务和人员管理。\n\n● 任职要求：\n1. 本科及以上应届毕业生，专业不限。\n2. 热爱零售行业，积极主动，有强烈的服务意识、团队合作意识，较强学习能力、沟通能力以及领导力。\n3. 能适应倒班，接受工作地调动。\n\n● 工作时间：\n1. 每周标准工作时间为40小时。\n2. 排班时间根据门店业务需求制定，有不同时间段的班次，比如早班和晚班，具体时间灵活安排，无固定项。\n3. 因行业特性，一般在周一至周五排休，周末和节假日需正常上班。\n\n● 工作地点：\n在山姆会员店的门店工作。优先志愿城市，但会根据公司现有门店的人员需求进行调配，最终工作城市和地点可能会调整。\n\n● 薪酬福利：\n包含管培生工资进阶计划、年底双薪、五险一金、带薪年休假、节日福利、员工折扣卡、山姆会员卡。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (25, '外派管培生', '「苏州·吴江」', '1.5-2万', '亨通集团有限公司', '[\"通信/网络设备\", \"10000人以上\", \"民营\"]', '本科', '经验不限', 373, '招2人', '[\"电气工程及自动化\"]', '2025-03-21 08:00:00', '外派管培生适用特殊定制的培训计划，直接在12家海外产业公司培训，涉及海外产业公司人资、财务、行政、供应链、中台制造执行等模块培训，培养方向为海外产业公司中高层管理人员，薪酬定制。\n\n欢迎意向在海外长期发展、提升国际化能力、拓宽全球化视野的同学投递！', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (26, '研发', '「徐州」', '面议', '晶斐（徐州）半导体科技有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：材料科学与工程', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (27, '物资采购', '「徐州」', '面议', '徐州矿务（集团)新疆天山矿业有限责任公司阿克苏热电分公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招7人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：工商管理及相关专业', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (28, '测绘工程', '「徐州」', '面议', '江苏苏北土地房地产资产评估测绘咨询有限公司', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：不限专业', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (29, '设备技术员', '「泰州」', '7000-14000元', '江苏申源集团', '[\"电子设备制造\", \"300-499人\", \"民营\"]', '本科', '无经验', 373, '招2人', '[\"材料类\"]', '2025-04-24 08:00:00', '经验要求不限，专业要求：电气及其自动化、机械设计制造及自动化、机械工程、能源与动力工程', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (30, '管培生（人力资源方向）', '「上海·闵行」', '1-1.5万', '爱琴海集团', '[\"物业管理\", \"500-999人\", \"合资\"]', '本科', '无经验', 373, '若干', '[\"信息暂无\"]', '2024-10-15 08:00:00', '工作职责：\n该职能负责公司的人力资源规划、人才招聘、人才发展、薪酬福利、绩效管理、员工管理等方面工作，通过提供领先的方法论和最优的解决方案，提升组织和人员能力，传承公司核心价值观\n任职资格：\n1、2025届应届本科及以上学历，专业不限；\n2、对商业领域有浓厚兴趣，愿意在此行业长期发展；\n3、具有优秀的商业兴趣和洞察力、较强的逻辑思维能力、敢于创新的探索精神、优秀的团队合作与沟通能力，不断寻求自我突破；\n4、具备一定的地域灵活性。（培养期内总部&项目多地域轮岗，后期结合个人意愿及公司发展确定工作地点）', '上海市闵行区申长路1466弄');
INSERT INTO `tb_job_posting` VALUES (31, '信息技术', '「徐州」', '面议', '沛县兴蓉水务发展有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招3人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：信息技术', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (32, '病案信息管理', '「丽水」', '1-1.2万', '龙泉市人民医院（浙江大学附属第二医院龙泉分院）', '[\"社团/组织/社会保障\", \"100-299人\", \"其它\"]', '本科', '经验不限', 373, '招1人', '[\"临床医学\"]', '2025-03-20 08:00:00', '35周岁及以下（1988年3月26日以后出生）,本科及以上学历，专业要求：临床医学类，卫生事业管理', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (33, 'AI产品经理', '「上海」', '面议', '上海复星高科技（集团）有限公司', '[\"零售/批发\", \"10000人以上\", \"上市公司\"]', '本科', '经验不限', 373, '若干', '[\"信息暂无\"]', '2025-04-23 08:00:00', '职位描述\n1. 参与AI技术在各产业（健康/消费/金融/制造等）的场景挖掘，完成需求分析、原型设计及产品落地；\n2. 协同算法与工程团队，推动AI技术转化为可落地的解决方案（如智能客服、AI辅助诊断、营销自动化工具等）；\n3. 跟踪AI行业趋势与竞品动态，输出产品迭代策略与商业化路径规划；\n4. 负责用户反馈收集、数据分析及产品效果评估，持续优化用户体验；\n5. 协助完成产品文档撰写、跨部门沟通及项目管理。\n\n职位要求\n硬性要求：\n1. 本科及以上学历，计算机、软件工程、信息管理、工业设计等相关专业（理工科背景优先）；\n2. 了解AI技术基础（如机器学习、NLP、CV等），熟悉主流AI平台工具（如Hugging Face、Azure AI等）；\n3. 具备产品思维，擅长逻辑分析及需求拆解，有产品设计/项目管理实习经验者优先；\n4. 熟练使用Axure/Figma/Sketch等原型工具，掌握SQL/Excel基础数据分析能力；\n5. 对AI技术商业化有强烈兴趣，关注AI+行业（如AIGC、智能硬件、产业数字化）创新场景。\n\n加分项：\n- 参与过AI产品全生命周期项目（需求-设计-上线-运营）；\n- 熟悉产品商业化模式（如SaaS、API服务、硬件集成）；\n- 有健康/消费/金融/制造等行业研究或实习经历，能快速理解业务痛点；\n- 具备基础编程能力（Python/JavaScript），可与技术团队高效协作。\n\n软技能要求：\n1. 用户导向：能从业务场景出发设计产品，平衡技术可行性与用户体验；\n2. 沟通能力：擅长跨团队协作，能将技术语言转化为业务价值；\n3. 商业敏感度：对AI产品的市场价值、成本收益有基础认知。', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (34, '化工类', '「徐州」', '面议', '徐州矿务集团有限公司', '[\"通用设备制造\", \"500-999人\", \"国企\"]', '本科', '无经验', 373, '招6人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：应用化学、高分子材料与工程等', '信息暂无');
INSERT INTO `tb_job_posting` VALUES (35, '运营管培生', '「无锡·锡山」', '8000-10000元·13薪', '确成股份', '[\"化工\", \"100-299人\", \"股份制企业\"]', '本科', '无经验', 373, '招10人', '[\"材料化学, 分析化学\"]', '2025-04-22 08:00:00', '1、学习与培养：接受公司的培训，学习公司战略、文化、经营模式和管理理念。通过内部培训课程、研讨会和导师指导等方式，了解公司的各个职能部门、流程、和运营模式，并学习解决问题和决策的基本技能。\n2、轮岗实践：参与各个只能部门的轮岗实践。在一定的时间内在不同的职位上工作，深入了解各部门的工作内容和流程，并从实践中学习解决问题的能力和跨部门协作的能力。', '确成硅化学股份有限公司青港路25号');
INSERT INTO `tb_job_posting` VALUES (36, '体育老师', '「徐州」', '面议', '沛县汉城文昌学校', '[\"通用设备制造\", \"500-999人\", \"民营\"]', '本科', '无经验', 373, '招15人', '[\"材料类\"]', '2025-03-12 08:00:00', '学历要求：本科\n专业要求：体育老师', '信息暂无');

-- ----------------------------
-- Table structure for tb_news
-- ----------------------------
DROP TABLE IF EXISTS `tb_news`;
CREATE TABLE `tb_news`  (
  `news_id` int NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '新闻标题',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '新闻正文',
  `image_urls` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '新闻图片URL（多个图片以 | 隔开）',
  `author_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文章作者（如记者）',
  `publisher_id` int NULL DEFAULT NULL COMMENT '发布者ID（关联tb_person_info）',
  `publish_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `read_count` int NULL DEFAULT 0 COMMENT '阅读量',
  `tags` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '新闻标签（如校园新闻、就业动态等）',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标志（0=正常，1=已删除）',
  PRIMARY KEY (`news_id`) USING BTREE,
  INDEX `idx_publisher`(`publisher_id` ASC) USING BTREE,
  CONSTRAINT `tb_news_tb_person_info` FOREIGN KEY (`publisher_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '新闻资讯表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_news
-- ----------------------------

-- ----------------------------
-- Table structure for tb_news_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_news_comment`;
CREATE TABLE `tb_news_comment`  (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `news_id` int NOT NULL COMMENT '对应的新闻ID',
  `user_id` int NOT NULL COMMENT '评论人ID（tb_person_info.person_id）',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_news`(`news_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_news` FOREIGN KEY (`news_id`) REFERENCES `tb_news` (`news_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '新闻评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_news_comment
-- ----------------------------

-- ----------------------------
-- Table structure for tb_organization_num
-- ----------------------------
DROP TABLE IF EXISTS `tb_organization_num`;
CREATE TABLE `tb_organization_num`  (
  `num_id` int NOT NULL AUTO_INCREMENT,
  `sum` int NOT NULL,
  `class_id` int NULL DEFAULT NULL,
  `college_id` int NULL DEFAULT NULL,
  `specialty_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`num_id`) USING BTREE,
  INDEX `tb_organization_num_tb_class_grade`(`class_id` ASC) USING BTREE,
  INDEX `tb_organization_num_tb_college`(`college_id` ASC) USING BTREE,
  INDEX `tb_organization_num_tb_specialty`(`specialty_id` ASC) USING BTREE,
  CONSTRAINT `tb_organization_num_tb_class_grade` FOREIGN KEY (`class_id`) REFERENCES `tb_class_grade` (`class_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_organization_num_tb_college` FOREIGN KEY (`college_id`) REFERENCES `tb_college` (`college_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_organization_num_tb_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `tb_specialty` (`specialty_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_organization_num
-- ----------------------------
INSERT INTO `tb_organization_num` VALUES (1, 40, 1, 1, 1);
INSERT INTO `tb_organization_num` VALUES (2, 35, 2, 2, 2);

-- ----------------------------
-- Table structure for tb_person_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_person_info`;
CREATE TABLE `tb_person_info`  (
  `person_id` int NOT NULL AUTO_INCREMENT,
  `enable_Status` int NULL DEFAULT 0,
  `person_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `college_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`person_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_person_info
-- ----------------------------
INSERT INTO `tb_person_info` VALUES (3, 4, '超级管理员', '2020-03-06 19:27:50', 'admin1', 'admin', NULL);

-- ----------------------------
-- Table structure for tb_presentation
-- ----------------------------
DROP TABLE IF EXISTS `tb_presentation`;
CREATE TABLE `tb_presentation`  (
  `presentation_id` int NOT NULL AUTO_INCREMENT COMMENT '宣讲会ID',
  `company_id` int NOT NULL COMMENT '公司人员ID，对应tb_person_info.person_id，enable_status=2',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '宣讲会主题/标题',
  `location` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '举办地点（如教室编号或地址）',
  `capacity` int NOT NULL COMMENT '场地可容纳人数',
  `start_time` datetime NOT NULL COMMENT '宣讲会开始时间',
  `end_time` datetime NOT NULL COMMENT '宣讲会结束时间',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '宣讲会简介/说明',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0=待审批，1=已通过，2=已拒绝）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注说明',
  `signup_count` int NULL DEFAULT 0 COMMENT '当前报名人数',
  PRIMARY KEY (`presentation_id`) USING BTREE,
  INDEX `idx_company_id`(`company_id` ASC) USING BTREE,
  CONSTRAINT `fk_presentation_company` FOREIGN KEY (`company_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '宣讲会申请记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_presentation
-- ----------------------------

-- ----------------------------
-- Table structure for tb_presentation_signup
-- ----------------------------
DROP TABLE IF EXISTS `tb_presentation_signup`;
CREATE TABLE `tb_presentation_signup`  (
  `signup_id` int NOT NULL AUTO_INCREMENT COMMENT '报名记录ID',
  `presentation_id` int NOT NULL COMMENT '宣讲会ID',
  `student_id` int NOT NULL COMMENT '报名学生ID（tb_person_info.person_id, enable_status=0）',
  `signup_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  PRIMARY KEY (`signup_id`) USING BTREE,
  UNIQUE INDEX `uniq_pres_student`(`presentation_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `fk_signup_student`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_signup_presentation` FOREIGN KEY (`presentation_id`) REFERENCES `tb_presentation` (`presentation_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_signup_student` FOREIGN KEY (`student_id`) REFERENCES `tb_person_info` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '宣讲会报名表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_presentation_signup
-- ----------------------------

-- ----------------------------
-- Table structure for tb_specialty
-- ----------------------------
DROP TABLE IF EXISTS `tb_specialty`;
CREATE TABLE `tb_specialty`  (
  `specialty_id` int NOT NULL AUTO_INCREMENT,
  `specialty_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `college_id` int NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`specialty_id`) USING BTREE,
  INDEX `tb_specialty_tb_college`(`college_id` ASC) USING BTREE,
  CONSTRAINT `tb_specialty_tb_college` FOREIGN KEY (`college_id`) REFERENCES `tb_college` (`college_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_specialty
-- ----------------------------
INSERT INTO `tb_specialty` VALUES (1, '软件工程', 1, '2025-06-25 22:22:23');
INSERT INTO `tb_specialty` VALUES (2, '信息管理', 2, '2025-06-25 22:22:23');

-- ----------------------------
-- Table structure for tb_unit_kind
-- ----------------------------
DROP TABLE IF EXISTS `tb_unit_kind`;
CREATE TABLE `tb_unit_kind`  (
  `unit_id` int NOT NULL AUTO_INCREMENT,
  `unit_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`unit_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_unit_kind
-- ----------------------------
INSERT INTO `tb_unit_kind` VALUES (1, '国企', '2020-03-06 13:56:23');
INSERT INTO `tb_unit_kind` VALUES (2, '私企', '2020-03-06 13:56:29');
INSERT INTO `tb_unit_kind` VALUES (3, '公务员', '2020-03-06 13:56:36');
INSERT INTO `tb_unit_kind` VALUES (4, '事业单位', '2020-03-06 13:56:43');

SET FOREIGN_KEY_CHECKS = 1;
