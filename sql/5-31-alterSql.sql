
use online;
ALTER  TABLE notarzation_master ADD COLUMN notarialOfficeId BIGINT(10)    NOT NULL  DEFAULT 1  COMMENT  '公证处id'  AFTER  `notarzationTypeCode`;
CREATE TABLE `user_notarization` (
  `id` varchar(50) NOT NULL,
  `userId` varchar(50) NOT NULL COMMENT '用户id',
  `openId` varchar(200) NOT NULL COMMENT '用户openId',
  `notarialOfficeId` bigint(10) NOT NULL COMMENT '公证处id',
  `createdTime` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


use online-mgmt;
ALTER  TABLE sys_user ADD COLUMN signUrl VARCHAR(500)   COMMENT  '签名地址'  AFTER  `status`;
ALTER  TABLE sys_user ADD COLUMN notarialOfficeId BIGINT(10) NOT NULL  COMMENT  '公证处id'  AFTER  `signUrl`;
ALTER TABLE `sys_user` ADD COLUMN `notaryNum` varchar(255) NULL COMMENT '公证处求真编号' AFTER `notarialOfficeId`;
CREATE TABLE `sys_notarial_office` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '机构id',
  `notaryOfficeName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '机构名称',
  `sealUrl` varchar(500) DEFAULT NULL COMMENT '公章地址',
  `baseUrl` varchar(255) DEFAULT NULL COMMENT '文件服务baseurl',
  `insideIp` varchar(255) DEFAULT NULL COMMENT '文件服务内网地址',
  `secretKey` varchar(255) DEFAULT NULL COMMENT '文件token加密密钥',
  `notaryOfficeNum` varchar(255) DEFAULT NULL COMMENT '公证处求真编号',
  `receiveAddress` varchar(500) DEFAULT NULL COMMENT '自取接收地址',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1删除 2停用',
  `createdTime` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `createdBy` bigint(20) NOT NULL,
  `updatedTime` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updatedBy` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;


ALTER TABLE `document` ADD COLUMN `storePath` varchar(255) NULL COMMENT '文件服务器相对路径' AFTER `uploadedAbsolutePath`,
ADD COLUMN `storeGroup` varchar(255) NULL COMMENT '文件服务器组名' AFTER `storePath`;