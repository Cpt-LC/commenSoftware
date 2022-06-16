/*
 Navicat Premium Data Transfer

 Source Server         : kunshan
 Source Server Type    : MySQL
 Source Server Version : 50650
 Source Host           : 192.168.2.52:3306
 Source Schema         : online-log

 Target Server Type    : MySQL
 Target Server Version : 50650
 File Encoding         : 65001

 Date: 24/12/2021 11:58:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `logTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `url` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `requestId` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `duration` int(11) NULL DEFAULT NULL,
  `level` int(11) NULL DEFAULT NULL COMMENT 'info:30\nerror:40\nfalt',
  `method` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `serviceName` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pid` int(11) NULL DEFAULT NULL,
  `hostname` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `statusCode` int(11) NULL DEFAULT NULL,
  `clientIp` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `referer` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `requestHeaders` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `requestParams` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `requestBody` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '如果是文件，则有值',
  `responseHeaders` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `responseBody` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `ix_request_log_202109_time_url`(`logTime`) USING BTREE,
  INDEX `ix_request_log_202109_req_id`(`requestId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12542 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '请求日志' ROW_FORMAT = COMPACT;

DROP TABLE IF EXISTS `sys_specific_log`;
CREATE TABLE `sys_specific_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `logTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `url` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `requestId` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `level` int DEFAULT NULL COMMENT 'info:30\nerror:40\nfalt',
  `method` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `serviceName` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `pid` int DEFAULT NULL,
  `hostname` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `statusCode` int DEFAULT NULL,
  `clientIp` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `referer` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `requestHeaders` longtext CHARACTER SET utf8 COLLATE utf8_general_ci,
  `requestParams` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `requestBody` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `responseHeaders` longtext CHARACTER SET utf8 COLLATE utf8_general_ci,
  `responseBody` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `mark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '日志备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `ix_request_log_202109_time_url` (`logTime`) USING BTREE,
  KEY `ix_request_log_202109_req_id` (`requestId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60799 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='请求日志';

SET FOREIGN_KEY_CHECKS = 1;
