/*
 Navicat Premium Dump SQL

 Source Server         : 容器启动mysql
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : cvitem

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 10/05/2025 20:44:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（唯一）',
  `password` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希值',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` enum('user','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '用户角色：普通用户或管理员',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `details_id` int NULL DEFAULT NULL COMMENT '与detail相连',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `fk_account_details`(`details_id` ASC) USING BTREE,
  CONSTRAINT `fk_account_details` FOREIGN KEY (`details_id`) REFERENCES `account_details` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES (1, 'test1', '$2a$10$Md2WTLZKe/.yYDt3npXrJ.zeQY8lelyy.BtA5E4Su7z0dJ4q5fq0e', '123@qq.com', 'admin', '/avatar/4aa54576fc914ee697380d43605c8f1f', '2025-04-20 22:08:34', 1);
INSERT INTO `account` VALUES (2, 'test22', '$2a$10$9tXU.2NdazbT2Y8uGe/gP.Oxq.0CCqdSMo/3OZPJbgQlg1FGS5y5q', '1234@qq.com', 'user', '/avatar/a581539ace0c4647a343899111c769e0', '2025-04-20 22:08:34', 2);
INSERT INTO `account` VALUES (4, '1234', '$2a$10$LmkaCSARuqN5j1HcS.ZqJe.nH5QkT6A98Q6m0szq4vBktXLo0JY56', 'jian-0925@qq.com', 'user', NULL, '2025-05-10 10:13:02', 4);
INSERT INTO `account` VALUES (5, '1234567', '$2a$10$qFd4jmQpp8KrUtUZy75yf.JQX.ekInGsNmKpT8mwDKgZyAtlxNs2y', '12345@qq.com', 'user', NULL, '2025-05-10 10:22:18', 5);
INSERT INTO `account` VALUES (6, '12345678', '$2a$10$0ugjvxvnhL8pJjmVvWDTD.HaPXTdgpzvVPVhkIoZI9c44ebOWviy.', '2845038457@qq.com', 'user', NULL, '2025-05-10 10:49:41', 6);

-- ----------------------------
-- Table structure for account_details
-- ----------------------------
DROP TABLE IF EXISTS `account_details`;
CREATE TABLE `account_details`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `gender` tinyint NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `qq` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `wx` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '个人简介',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of account_details
-- ----------------------------
INSERT INTO `account_details` VALUES (1, 1, '123455', '123', '123', '1231', '肇庆');
INSERT INTO `account_details` VALUES (2, 2, '234', '324', '234', '2345', '肇庆');
INSERT INTO `account_details` VALUES (4, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `account_details` VALUES (5, 1, '1234', NULL, NULL, NULL, NULL);
INSERT INTO `account_details` VALUES (6, 1, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for bill_pdf
-- ----------------------------
DROP TABLE IF EXISTS `bill_pdf`;
CREATE TABLE `bill_pdf`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meter_id` int UNSIGNED NOT NULL COMMENT '对应表计ID',
  `bill_month` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账单月份（格式：YYYY-MM）',
  `pdf_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'PDF 文件存储地址',
  `total_cost` decimal(10, 2) NOT NULL COMMENT '该月总费用',
  `generated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_meter_month`(`meter_id` ASC, `bill_month` ASC) USING BTREE,
  CONSTRAINT `fk_bill_meter` FOREIGN KEY (`meter_id`) REFERENCES `meter` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'PDF 账单索引表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bill_pdf
-- ----------------------------
INSERT INTO `bill_pdf` VALUES (1, 1, '2025-04', 'https://minio.example.com/bills/305-水表-2025-04.pdf', 28.50, '2025-04-20 22:08:34');

-- ----------------------------
-- Table structure for meter
-- ----------------------------
DROP TABLE IF EXISTS `meter`;
CREATE TABLE `meter`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` enum('water','electricity','gas') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表计类型：水、电、气',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '位置描述（如房间号、门牌）',
  `user_id` int UNSIGNED NOT NULL COMMENT '所属用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_meter_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_meter_user` FOREIGN KEY (`user_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '表计信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of meter
-- ----------------------------
INSERT INTO `meter` VALUES (1, 'water', '宿舍305-水表', 2);
INSERT INTO `meter` VALUES (2, 'electricity', '宿舍305-电表', 2);
INSERT INTO `meter` VALUES (3, 'water', '达瓦', 1);
INSERT INTO `meter` VALUES (4, 'water', '厨房', 1);
INSERT INTO `meter` VALUES (5, 'water', '澡堂', 1);
INSERT INTO `meter` VALUES (7, 'electricity', '厨房', 1);
INSERT INTO `meter` VALUES (8, 'gas', '厨房', 1);

-- ----------------------------
-- Table structure for reading
-- ----------------------------
DROP TABLE IF EXISTS `reading`;
CREATE TABLE `reading`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meter_id` int UNSIGNED NULL DEFAULT NULL COMMENT '对应表计ID',
  `shot_time` datetime(3) NOT NULL COMMENT '拍摄/上传时间',
  `value` decimal(10, 3) NOT NULL COMMENT '本次读数',
  `delta` decimal(10, 3) NULL DEFAULT NULL COMMENT '本次用量（与上次差值）',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '本次费用',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原图存储地址',
  `preview_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标注图地址（识别结果叠加）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_meter_time`(`meter_id` ASC, `shot_time` ASC) USING BTREE,
  INDEX `idx_meter`(`meter_id` ASC) USING BTREE,
  INDEX `idx_meter_time`(`meter_id` ASC, `shot_time` ASC) USING BTREE,
  CONSTRAINT `fk_reading_meter` FOREIGN KEY (`meter_id`) REFERENCES `meter` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '表读数记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reading
-- ----------------------------
INSERT INTO `reading` VALUES (1, 1, '2025-04-15 12:00:00.000', 12.345, 2.345, 4.69, 'https://minio.example.com/images/123.jpg', 'https://minio.example.com/previews/123_ocr.jpg');
INSERT INTO `reading` VALUES (2, NULL, '2025-05-05 21:16:09.444', 6.757, NULL, 11.35, '/water/c967616d54a04791b991a7e06ef8ec5b', NULL);
INSERT INTO `reading` VALUES (3, NULL, '2025-05-06 08:42:58.915', 6.757, NULL, 11.35, '/water/c01bc943749143a9bc5ce7860cebbee1', NULL);
INSERT INTO `reading` VALUES (4, NULL, '2025-05-06 08:45:22.962', 6.757, NULL, 11.35, '/water/795bd18020a44683b3de45b503050f96', NULL);
INSERT INTO `reading` VALUES (5, NULL, '2025-05-06 08:46:43.713', 6.757, NULL, 11.35, '/water/4c9de677c5f94eeebb8bfdf466c0358f', NULL);
INSERT INTO `reading` VALUES (6, NULL, '2025-05-06 08:48:21.200', 6.757, NULL, 11.35, '/water/761f66fbe63f424ba5a3ec42ff2feaa5', NULL);
INSERT INTO `reading` VALUES (7, NULL, '2025-05-06 08:48:51.218', 6.757, NULL, 11.35, '/water/9a3c7fd11a424fe49aa2e08683b5f92d', NULL);
INSERT INTO `reading` VALUES (8, NULL, '2025-05-06 08:49:19.994', 6.757, NULL, 11.35, '/water/c1da98f0785a450b9ab0c1d7f6325126', NULL);
INSERT INTO `reading` VALUES (9, NULL, '2025-05-06 08:52:40.853', 6.757, NULL, 11.35, '/water/e5c9dda5e5d743419ab315eeee3fcdd1', NULL);
INSERT INTO `reading` VALUES (10, NULL, '2025-05-06 09:01:11.136', 6.757, NULL, 11.35, '/water/00a91d952c1d425984809e34594aeb03', NULL);
INSERT INTO `reading` VALUES (11, NULL, '2025-05-06 09:01:59.688', 6.757, NULL, 11.35, '/water/29cb985a10314d24b5fc21fa84a63c8d', NULL);
INSERT INTO `reading` VALUES (12, NULL, '2025-05-06 09:03:07.205', 6.757, NULL, 11.35, '/water/77dd51ddd033481599596a7e82eee577', NULL);
INSERT INTO `reading` VALUES (13, NULL, '2025-05-06 09:03:38.912', 6.757, NULL, 11.35, '/water/eafad0fd50624bc896c2a23728742fb1', NULL);
INSERT INTO `reading` VALUES (14, NULL, '2025-05-06 09:03:51.231', 6.757, NULL, 11.35, '/water/943e195924cd4f3eb7f891d5130dbfc5', NULL);
INSERT INTO `reading` VALUES (15, NULL, '2025-05-06 09:04:05.314', 6.757, NULL, 11.35, '/water/85c72c321ac64d96a4cdcd33f908d582', NULL);
INSERT INTO `reading` VALUES (16, NULL, '2025-05-06 09:08:25.392', 6.757, NULL, 11.35, '/water/c554f8a145fb413a9c90f6c99000a51d', NULL);
INSERT INTO `reading` VALUES (17, NULL, '2025-05-06 09:08:37.945', 6.757, NULL, 11.35, '/water/e603b678201f456f9915c7882b5e295d', NULL);
INSERT INTO `reading` VALUES (18, NULL, '2025-05-06 09:13:28.058', 6.757, NULL, 11.35, '/water/6e6b52e9ef474972a04675cd658b4a89', NULL);
INSERT INTO `reading` VALUES (19, NULL, '2025-05-06 09:13:43.014', 6.757, NULL, 11.35, '/water/104ff092ca884e7c80540e50ec8297ec', NULL);
INSERT INTO `reading` VALUES (20, NULL, '2025-05-06 09:20:54.997', 6.757, NULL, 11.35, '/water/32e456630c5d452c90e4e2c90ed61303', NULL);
INSERT INTO `reading` VALUES (21, NULL, '2025-05-06 09:22:30.218', 6.757, NULL, 11.35, '/water/d962ca22b3114f38bce1b8b7d36cb2ba', NULL);
INSERT INTO `reading` VALUES (22, NULL, '2025-05-06 09:23:22.137', 6.757, NULL, 11.35, '/water/ac2ee36c73f447a292ae20a63be6b81d', NULL);
INSERT INTO `reading` VALUES (23, NULL, '2025-05-06 09:23:30.891', 6.757, NULL, 11.35, '/water/0bb76c3b0ec84905a72fc73491203931', NULL);
INSERT INTO `reading` VALUES (24, NULL, '2025-05-06 09:23:38.702', 6.757, NULL, 11.35, '/water/34427d02ff4d4f2a83bb82cbd7736b83', NULL);
INSERT INTO `reading` VALUES (25, NULL, '2025-05-06 09:24:34.671', 6.757, NULL, 11.35, '/water/e7a50b7767264221a78b5340f2dc4adf', NULL);
INSERT INTO `reading` VALUES (26, NULL, '2025-05-06 09:32:01.027', 6.757, NULL, 11.35, '/water/24484b7090b341a4a005b6c912b65cef', NULL);
INSERT INTO `reading` VALUES (27, NULL, '2025-05-06 09:34:20.983', 6.757, NULL, 11.35, '/water/85a005e1bc0146ec9fab2e5ea098781a', NULL);
INSERT INTO `reading` VALUES (28, NULL, '2025-05-06 09:34:34.044', 2013.000, NULL, 9956.52, '/water/d65315baedaf4074bb8139854a8e3da8', NULL);
INSERT INTO `reading` VALUES (29, 3, '2025-05-06 10:46:13.139', 6.757, NULL, 11.35, '/water/c2ac5b13da99471f800eda060251e858', NULL);
INSERT INTO `reading` VALUES (30, 4, '2025-05-06 11:33:39.680', 6.757, NULL, 11.35, '/water/13dfbfb8d93b49319b816bcdb2e1ce0b', NULL);
INSERT INTO `reading` VALUES (31, 4, '2025-05-09 22:10:50.517', 6.757, NULL, 11.35, '/water/32db262664f94f96928287621e03738d', NULL);
INSERT INTO `reading` VALUES (32, 5, '2025-05-10 14:06:14.366', 2013.000, NULL, 9956.52, '/water/31f47110ab86420d8c3c55d84ba44ab0', NULL);
INSERT INTO `reading` VALUES (33, 4, '2025-05-10 14:39:14.572', 558.350, NULL, 7747.27, '/water/0804e432e1b845a0b6c0d242e7ec65af', NULL);
INSERT INTO `reading` VALUES (34, 4, '2025-05-10 14:59:23.229', 558.350, NULL, 2625.08, '/reading/d8bb821c59594561987555b60ed82131', NULL);
INSERT INTO `reading` VALUES (35, 4, '2025-05-10 15:01:12.242', 558.350, NULL, 7747.27, '/reading/a35eede793b1484dbab0835dadf02203', NULL);
INSERT INTO `reading` VALUES (36, 4, '2025-05-10 15:09:46.389', 558.350, NULL, 2625.08, '/reading/8eda287e7ae34201b12f2e69dc45bccd', NULL);
INSERT INTO `reading` VALUES (37, 4, '2025-05-10 15:11:37.847', 558.350, NULL, 2625.08, '/reading/4dbe49a802b8467eb196ab6fe6f15203', NULL);
INSERT INTO `reading` VALUES (39, 7, '2025-05-10 15:19:16.607', 558.350, NULL, 7747.27, '/reading/c28635d2ab084d358d734b4ab9094ad4', NULL);
INSERT INTO `reading` VALUES (40, 8, '2025-05-10 15:34:20.000', 89.200, NULL, 167.30, NULL, NULL);
INSERT INTO `reading` VALUES (41, 4, '2025-05-10 15:54:10.870', 2013.000, NULL, 9956.52, '/reading/8bfa371198df4ed09c846e57f217533c', NULL);

-- ----------------------------
-- Table structure for tariff_tier
-- ----------------------------
DROP TABLE IF EXISTS `tariff_tier`;
CREATE TABLE `tariff_tier`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `version_id` int NOT NULL COMMENT '所属版本ID',
  `seq` tinyint UNSIGNED NOT NULL COMMENT '阶梯编号（顺序）',
  `upper_bound` decimal(10, 3) NULL DEFAULT NULL COMMENT '此阶梯最大用量（含），为空表示最后一档',
  `price` decimal(10, 2) NOT NULL COMMENT '该阶梯对应的单价',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `version_id`(`version_id` ASC) USING BTREE,
  CONSTRAINT `tariff_tier_ibfk_1` FOREIGN KEY (`version_id`) REFERENCES `tariff_version` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tariff_tier_chk_1` CHECK ((`upper_bound` is null) or (`upper_bound` > 0))
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '阶梯价格配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tariff_tier
-- ----------------------------
INSERT INTO `tariff_tier` VALUES (1, 1, 1, 30.000, 1.68);
INSERT INTO `tariff_tier` VALUES (2, 1, 2, 35.000, 2.52);
INSERT INTO `tariff_tier` VALUES (3, 1, 3, NULL, 5.04);
INSERT INTO `tariff_tier` VALUES (4, 2, 1, 13.000, 1.80);
INSERT INTO `tariff_tier` VALUES (5, 2, 2, 17.000, 3.00);
INSERT INTO `tariff_tier` VALUES (6, 2, 3, NULL, 6.04);
INSERT INTO `tariff_tier` VALUES (7, 3, 1, 17.000, 2.00);
INSERT INTO `tariff_tier` VALUES (9, 3, 2, 36.000, 5.00);
INSERT INTO `tariff_tier` VALUES (28, 4, 1, 20.000, 12.00);
INSERT INTO `tariff_tier` VALUES (29, 4, 2, 40.000, 13.01);
INSERT INTO `tariff_tier` VALUES (30, 4, 3, NULL, 14.02);
INSERT INTO `tariff_tier` VALUES (31, 3, 3, NULL, 6.00);
INSERT INTO `tariff_tier` VALUES (32, 5, 1, 100.000, 2.50);
INSERT INTO `tariff_tier` VALUES (33, 5, 2, 120.000, 3.50);

-- ----------------------------
-- Table structure for tariff_version
-- ----------------------------
DROP TABLE IF EXISTS `tariff_version`;
CREATE TABLE `tariff_version`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` enum('water','electricity','gas') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对应表计类型',
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '版本名称或标识',
  `start_time` date NOT NULL COMMENT '生效开始时间',
  `end_time` date NULL DEFAULT '9999-12-31' COMMENT '生效结束时间（默认长期有效）',
  `is_active` tinyint NULL DEFAULT 0 COMMENT '是否是当前生效版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '价格版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tariff_version
-- ----------------------------
INSERT INTO `tariff_version` VALUES (1, 'water', 'v1.0.0', '2025-01-01', '9999-12-31', 1);
INSERT INTO `tariff_version` VALUES (2, 'water', 'v1.0.1', '2025-01-01', '9999-12-31', 0);
INSERT INTO `tariff_version` VALUES (3, 'electricity', 'v1.0.0', '2025-01-01', '9999-12-31', 1);
INSERT INTO `tariff_version` VALUES (4, 'electricity', 'v1.0.2', '2025-01-01', '9999-12-31', 0);
INSERT INTO `tariff_version` VALUES (5, 'electricity', 'v1.0.3', '2025-05-09', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
