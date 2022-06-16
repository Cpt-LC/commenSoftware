/*
 Navicat Premium Data Transfer

 Source Server         : 在线受理
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : 49.233.58.123:3306
 Source Schema         : online

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 11/01/2022 11:36:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for document
-- ----------------------------
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document`  (
  `id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `refererTableName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '相关表名',
  `refererId` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '相关表的主键',
  `categoryCode` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类code，来自于代码中的值，格式：公证事项/文件分类，比如，GR-HEAD',
  `fileName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `fileSize` bigint NOT NULL DEFAULT 0 COMMENT '文件大小，单位字节',
  `fileSizeM` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '文件大小，单位兆（M），便于阅读',
  `fileExt` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `uploadedRelativePath` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上传地址，相对路径',
  `uploadedAbsolutePath` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上传地址，绝对路径',
  `fileHash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件内容哈希',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDeleted` tinyint NOT NULL DEFAULT 0,
  `deletedTime` timestamp NULL DEFAULT NULL,
  `storageType` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'local' COMMENT '存储方式：local：本地',
  `chainHash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '链的哈希',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_UNIQUE`(`id`) USING BTREE,
  INDEX `ix_document_referer_table_id`(`refererTableName`, `refererId`, `categoryCode`, `createdTime`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '上传文件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notarzation_auth_comment
-- ----------------------------
DROP TABLE IF EXISTS `notarzation_auth_comment`;
CREATE TABLE `notarzation_auth_comment`  (
  `id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `notarzationId` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公证id',
  `tableName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `referrerId` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相应的子表主键。比如，如果tableName是document，那么referrerId就是document表的主键',
  `fieldName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '字段名',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '标注状态 0有错未修改；1有错已修改',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标注内容',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updatedBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FX_notarzation_master_auth_idx`(`notarzationId`) USING BTREE,
  CONSTRAINT `FX_notarzation_master_auth` FOREIGN KEY (`notarzationId`) REFERENCES `notarzation_master` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公证申请修改内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notarzation_certificate
-- ----------------------------
DROP TABLE IF EXISTS `notarzation_certificate`;
CREATE TABLE `notarzation_certificate`  (
  `certificateId` bigint NOT NULL AUTO_INCREMENT,
  `uuid` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`certificateId`) USING HASH,
  UNIQUE INDEX `UK_notarzation_certificate_uuid`(`uuid`) USING HASH
) ENGINE = MEMORY AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for notarzation_graduation
-- ----------------------------
DROP TABLE IF EXISTS `notarzation_graduation`;
CREATE TABLE `notarzation_graduation`  (
  `id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `graduatedFrom` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '毕业于哪所学校',
  `graduatedDate` date NOT NULL COMMENT '毕业时间',
  `isDeleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否有效；如果取消支付，或是其他原因',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updatedBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_UNIQUE`(`id`) USING BTREE,
  CONSTRAINT `fk_notarzation_master_gr` FOREIGN KEY (`id`) REFERENCES `notarzation_master` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '毕业公证申请详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notarzation_master
-- ----------------------------
DROP TABLE IF EXISTS `notarzation_master`;
CREATE TABLE `notarzation_master`  (
  `id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `orderId` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单id',
  `userId` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `realName` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `processNo` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '受理单号',
  `status` enum('PendingApproved','PendingConfirmed','PendingPayment','GeneratingCert','PendingPickup','Completed','Rejected','Canceled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PendingApproved' COMMENT '公证状态:待审核、待确认、待支付、出证中、待领取、已完成、已驳回、已取消',
  `processStatus` enum('Doing','Approving','GeneratingCert','Completed') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'Doing' COMMENT '办理中、待审批、出证中、已完成',
  `isAgent` tinyint NOT NULL DEFAULT 0 COMMENT '是否是代办',
  `applicantParty` enum('P','E') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'P' COMMENT '申请主体: P - 个人，E - 企业',
  `notarzationTypeCode` enum('GR','TAX') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'GR' COMMENT '公证事项类型,毕业证 - GR,TAX',
  `gender` int NULL DEFAULT NULL COMMENT '性别：1：男，2：女',
  `birth` date NULL DEFAULT NULL,
  `idCardType` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '身份证' COMMENT '证件类型',
  `idCardNo` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证件号码',
  `idCardAddress` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证地址。如果证件类型是身份证的时候，该值不为空',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `usedToCountry` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '使用地国家',
  `usedToProvince` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '使用地省份',
  `usedToCity` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '使用地城市',
  `copyNumber` int NOT NULL DEFAULT 0 COMMENT '所需份数',
  `translateTo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '翻译成哪种语言',
  `requiredVerification` tinyint NOT NULL DEFAULT 0 COMMENT '是否需要外事认证',
  `nationality` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国籍',
  `usedFor` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '其他' COMMENT '使用用途：探亲 学习 继承 就业 结婚 对外贸易 诉讼 招标投标 签订合同 减免税 定居 申请知识产权 提供劳务 扶养（抚养）亲属 领取养老金 其他',
  `hasMoreCert` tinyint NOT NULL DEFAULT 0 COMMENT '是否需要办理公证证明译文与原件相符',
  `hasForeign` tinyint NOT NULL DEFAULT 0 COMMENT '是否需要做外事认证',
  `sentToStraitsExchangeFoundation` int NOT NULL DEFAULT 0 COMMENT '需要寄台湾海基会的副本份数',
  `expressModeToSEF` enum('P','S') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公证书邮寄到台湾海基会的邮寄方式: P: 普通挂号邮寄;  S:特快专递',
  `hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '链的哈希',
  `signedReceipt` tinyint NOT NULL DEFAULT 0 COMMENT '是否已签收送达回执',
  `actionBy` bigint NULL DEFAULT NULL COMMENT '操作者，办证公证员',
  `recordBy` bigint NULL DEFAULT NULL COMMENT '笔录者，笔录员',
  `userRemark` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户备注',
  `authComment` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核意见',
  `staffRemark` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公证员备注',
  `isDeleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否有效；如果取消支付，或是其他原因',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updatedBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_UNIQUE`(`id`) USING BTREE,
  UNIQUE INDEX `uk_notarzation_master_process_no`(`processNo`) USING BTREE,
  INDEX `fk_notarzation_user_idx`(`userId`) USING BTREE,
  INDEX `fk_notarzation_order_idx`(`orderId`) USING BTREE,
  CONSTRAINT `fk_notarzation_order` FOREIGN KEY (`orderId`) REFERENCES `order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_notarzation_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公证申请主体表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userId` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `orderNo` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号：<前缀两位>+年月日8位+时间戳后6位+随机数6位',
  `status` enum('PendingApproved','PendingConfirmed','PendingPayment','GeneratingCert','PendingPickup','Completed','Rejected','Canceled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PendingApproved' COMMENT '公证状态:待审核、待确认、待支付、出证中、待领取、已完成、已驳回、已取消',
  `paymentMode` enum('Online','Offline') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'Online' COMMENT '支付方式，线上支付或线下支付',
  `paymentType` enum('wechat','ali') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'wechat' COMMENT '支付类型',
  `paymentStatus` enum('NotPaid','Paid','Canceled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NotPaid' COMMENT '支付状态',
  `realAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际金额',
  `paidAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '已付金额',
  `notaryAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '公证费',
  `copyAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '副本费',
  `translationAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '翻译费',
  `logisticsAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '快递费',
  `serviceAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '公证服务费',
  `outtradeNo` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商户订单号',
  `paymentTime` timestamp NULL DEFAULT NULL,
  `isDeleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否有效；如果取消支付，或是其他原因',
  `logisticsCompany` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流公司',
  `logisticsNumber` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `isSend` tinyint NOT NULL DEFAULT 0 COMMENT '是否邮寄',
  `sentToProvince` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮寄省',
  `sentToCity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮寄城市',
  `sentToArea` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮寄区域',
  `sentToAddress` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮寄地址',
  `sentToPhone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮寄电话号',
  `sendtToName` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件人',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updatedBy` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_UNIQUE`(`id`) USING BTREE,
  INDEX `fk_order_user_idx`(`userId`) USING BTREE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `realName` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '加密后的密码',
  `avatarUrl` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `lastLoginTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `gender` int NULL DEFAULT NULL COMMENT '性别：1：男，2：女',
  `birth` date NULL DEFAULT NULL,
  `idCardType` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '身份证' COMMENT '证件类型',
  `idCardNo` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证件号码',
  `nationality` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国籍',
  `appidIndex` int NOT NULL DEFAULT 1 COMMENT '项目索引号',
  `loginFailedCount` int NOT NULL DEFAULT 0 COMMENT '登录失败次数',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_UNIQUE`(`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表。可兼容多个项目用户数据' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
