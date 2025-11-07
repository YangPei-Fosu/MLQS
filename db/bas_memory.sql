/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80100
 Source Host           : 127.0.0.1:3306
 Source Schema         : fsgs

 Target Server Type    : MySQL
 Target Server Version : 80100
 File Encoding         : 65001

 Date: 07/11/2025 17:18:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bas_memory
-- ----------------------------
DROP TABLE IF EXISTS `bas_memory`;
CREATE TABLE `bas_memory`  (
  `session_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话id',
  `memory` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '序列化记忆',
  `create_time` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` bit(1) NULL DEFAULT b'0' COMMENT '0未弃用，1已经弃用',
  PRIMARY KEY (`session_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
