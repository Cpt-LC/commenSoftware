/*
 Navicat Premium Data Transfer

 Source Server         : kunshan
 Source Server Type    : MySQL
 Source Server Version : 50650
 Source Host           : 192.168.2.52:3306
 Source Schema         : online-mgmt

 Target Server Type    : MySQL
 Target Server Version : 50650
 File Encoding         : 65001

 Date: 18/03/2022 13:34:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_captcha
-- ----------------------------
DROP TABLE IF EXISTS `sys_captcha`;
CREATE TABLE `sys_captcha`  (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'uuid',
  `code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
  `expiredTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统验证码' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_captcha
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(36) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
  `time` bigint(20) NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `createdTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint(36) NOT NULL AUTO_INCREMENT,
  `parentId` bigint(36) NULL DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单URL',
  `permissions` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `orderNum` int(11) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单管理' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '系统管理', NULL, NULL, 0, 'system', 0);
INSERT INTO `sys_menu` VALUES (2, 1, '用户管理', 'sys/user', NULL, 1, 'admin', 1);
INSERT INTO `sys_menu` VALUES (3, 1, '角色管理', 'sys/role', NULL, 1, 'role', 2);
INSERT INTO `sys_menu` VALUES (4, 1, '菜单管理', 'sys/menu', NULL, 1, 'menu', 3);
INSERT INTO `sys_menu` VALUES (15, 2, '查看', NULL, 'sys:user:list,sys:user:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (16, 2, '新增', NULL, 'sys:user:save,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (17, 2, '修改', NULL, 'sys:user:update,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (18, 2, '删除', NULL, 'sys:user:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (19, 3, '查看', NULL, 'sys:role:list,sys:role:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (20, 3, '新增', NULL, 'sys:role:save,sys:menu:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (21, 3, '修改', NULL, 'sys:role:update,sys:menu:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (22, 3, '删除', NULL, 'sys:role:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (23, 4, '查看', NULL, 'sys:menu:list,sys:menu:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (24, 4, '新增', NULL, 'sys:menu:save,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (25, 4, '修改', NULL, 'sys:menu:update,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (26, 4, '删除', NULL, 'sys:menu:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (46, 0, '公证事项', NULL, NULL, 0, 'menu', 2);
INSERT INTO `sys_menu` VALUES (52, 0, '存证事项', '', '', 0, 'editor', 3);
INSERT INTO `sys_menu` VALUES (67, 46, '受理列表', 'list/index', NULL, 1, 'menu', 0);
INSERT INTO `sys_menu` VALUES (68, 67, '认领', 'list/index', 'sys:cert:claim', 2, 'menu', 0);
INSERT INTO `sys_menu` VALUES (69, 67, '详情查看', 'list/index', 'sys:cert:detail', 2, 'menu', 0);
INSERT INTO `sys_menu` VALUES (70, 67, '公证员审核', 'list/index', 'sys:cert:detail:notary', 2, 'menu', 0);
INSERT INTO `sys_menu` VALUES (71, 67, '主任审核', 'list/index', 'sys:cert:detail:direct', 2, 'menu', 0);
INSERT INTO `sys_menu` VALUES (76, 52, '存证数据列表', 'saveEvidence/saveEvidenceDataList', NULL, 1, 'config', 0);
INSERT INTO `sys_menu` VALUES (77, 52, '存证用户列表', 'saveEvidence/saveEvidenceUserList', '', 1, 'geren', 0);
INSERT INTO `sys_menu` VALUES (78, 0, '商标公证', '', '', 0, 'log', 4);
INSERT INTO `sys_menu` VALUES (81, 78, '受理列表', 'trademark/index', NULL, 1, 'log', 0);
INSERT INTO `sys_menu` VALUES (82, 81, '认领', 'trademark/index', 'sys:cert:claim', 2, '', 0);
INSERT INTO `sys_menu` VALUES (83, 81, '详情查看', 'trademark/index', 'sys:cert:detail', 2, '', 0);
INSERT INTO `sys_menu` VALUES (84, 81, '公证员审核', 'trademark/index', 'sys:cert:detail:notary', 2, '', 0);
INSERT INTO `sys_menu` VALUES (85, 81, '主任审核', 'trademark/index', 'sys:cert:detail:markNotary', 2, '', 0);
INSERT INTO `sys_menu` VALUES (86, 0, '企业账号管理', '', '', 0, 'zhedie', 5);
INSERT INTO `sys_menu` VALUES (87, 86, '企业申请列表', 'applyList/index', NULL, 1, 'zhedie', 0);
-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(36) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `createdBy` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `createdTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', '超级管理员', 1, '2021-12-29 17:40:59');
INSERT INTO `sys_role` VALUES (2, '管理员', '管理员', 1, '2021-12-29 17:40:59');
INSERT INTO `sys_role` VALUES (13, '公证员', '公证人员', 1, '2021-12-29 17:40:59');
INSERT INTO `sys_role` VALUES (14, '主任', '主任', 1, '2021-12-29 17:40:59');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint(36) NOT NULL AUTO_INCREMENT,
  `roleId` bigint(36) NULL DEFAULT NULL COMMENT '角色ID',
  `menuId` bigint(36) NULL DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 845 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色与菜单对应关系' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (784, 1, 1);
INSERT INTO `sys_role_menu` VALUES (785, 1, 2);
INSERT INTO `sys_role_menu` VALUES (786, 1, 15);
INSERT INTO `sys_role_menu` VALUES (787, 1, 16);
INSERT INTO `sys_role_menu` VALUES (788, 1, 17);
INSERT INTO `sys_role_menu` VALUES (789, 1, 18);
INSERT INTO `sys_role_menu` VALUES (790, 1, 3);
INSERT INTO `sys_role_menu` VALUES (791, 1, 19);
INSERT INTO `sys_role_menu` VALUES (792, 1, 20);
INSERT INTO `sys_role_menu` VALUES (793, 1, 21);
INSERT INTO `sys_role_menu` VALUES (794, 1, 22);
INSERT INTO `sys_role_menu` VALUES (795, 1, 4);
INSERT INTO `sys_role_menu` VALUES (796, 1, 23);
INSERT INTO `sys_role_menu` VALUES (797, 1, 24);
INSERT INTO `sys_role_menu` VALUES (798, 1, 25);
INSERT INTO `sys_role_menu` VALUES (799, 1, 26);
INSERT INTO `sys_role_menu` VALUES (800, 1, 48);
INSERT INTO `sys_role_menu` VALUES (801, 1, 49);
INSERT INTO `sys_role_menu` VALUES (802, 1, 50);
INSERT INTO `sys_role_menu` VALUES (803, 1, 51);
INSERT INTO `sys_role_menu` VALUES (804, 1, -666666);
INSERT INTO `sys_role_menu` VALUES (805, 1, 46);
INSERT INTO `sys_role_menu` VALUES (806, 1, 47);
INSERT INTO `sys_role_menu` VALUES (807, 2, 1);
INSERT INTO `sys_role_menu` VALUES (808, 2, 2);
INSERT INTO `sys_role_menu` VALUES (809, 2, 15);
INSERT INTO `sys_role_menu` VALUES (810, 2, 16);
INSERT INTO `sys_role_menu` VALUES (811, 2, 17);
INSERT INTO `sys_role_menu` VALUES (812, 2, 18);
INSERT INTO `sys_role_menu` VALUES (813, 2, 49);
INSERT INTO `sys_role_menu` VALUES (814, 2, -666666);
INSERT INTO `sys_role_menu` VALUES (815, 2, 46);
INSERT INTO `sys_role_menu` VALUES (816, 2, 47);
INSERT INTO `sys_role_menu` VALUES (823, 14, 49);
INSERT INTO `sys_role_menu` VALUES (824, 14, 51);
INSERT INTO `sys_role_menu` VALUES (825, 14, -666666);
INSERT INTO `sys_role_menu` VALUES (826, 14, 46);
INSERT INTO `sys_role_menu` VALUES (827, 14, 47);
INSERT INTO `sys_role_menu` VALUES (835, 13, 46);
INSERT INTO `sys_role_menu` VALUES (836, 13, 67);
INSERT INTO `sys_role_menu` VALUES (837, 13, 68);
INSERT INTO `sys_role_menu` VALUES (838, 13, 69);
INSERT INTO `sys_role_menu` VALUES (839, 13, 70);
INSERT INTO `sys_role_menu` VALUES (840, 13, 71);
INSERT INTO `sys_role_menu` VALUES (841, 13, 52);
INSERT INTO `sys_role_menu` VALUES (842, 13, 76);
INSERT INTO `sys_role_menu` VALUES (843, 13, 77);
INSERT INTO `sys_role_menu` VALUES (844, 13, -666666);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(36) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `realName` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `salt` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `createdBy` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `createdTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'super-admin', '9ec9750e709431dad22365cabc5c625482e574c74adaebba7dd02f1129e4ce1d', '超级管理员', 'YzcmCZNvbXocrsz9dm8e', '', '', 1, 1, '2021-12-29 17:41:01');
INSERT INTO `sys_user` VALUES (2, 'admin', '9ec9750e709431dad22365cabc5c625482e574c74adaebba7dd02f1129e4ce1d', '管理员', 'YzcmCZNvbXocrsz9dm8e', '', '', 1, 1, '2021-12-29 17:41:01');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(36) NOT NULL AUTO_INCREMENT,
  `userId` bigint(36) NULL DEFAULT NULL COMMENT '用户ID',
  `roleId` bigint(36) NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与角色对应关系' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (31, 1, 1);
INSERT INTO `sys_user_role` VALUES (32, 2, 2);

-- ----------------------------
-- Table structure for sys_user_token
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_token`;
CREATE TABLE `sys_user_token`  (
  `userId` bigint(36) NOT NULL AUTO_INCREMENT,
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'token',
  `expiredTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `updatedTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE INDEX `token`(`token`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户Token' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of sys_user_token
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
