

alter table notarzation_master add column companyName  varchar(50) comment '企业名称' after isDeleted;
alter table notarzation_master add column socialCreditCode  varchar(30) comment '社会信用代码' after companyName;
alter table notarzation_master add column legalRepresentative  varchar(50) comment '企业法人代表' after SocialCreditCode;
alter table notarzation_master add column companyAddress  varchar(50) comment '企业地址' after legalRepresentative;
alter table notarzation_master add column legalStatus  tinyint(4) comment '办理人是否是法人代表：0-不是，1-是' after companyAddress;
alter table notarzation_master add column usedToRemark  varchar(500) comment '使用地说明' after usedToCity;
alter table notarzation_master add column usedForRemark  varchar(500) comment '用途说明' after usedFor;

ALTER TABLE `notarzation_master` CHANGE `notarzationTypeCode` `notarzationTypeCode` ENUM ('GR', 'TAX', 'DL', 'DC', 'NC','WTMFB01','WTMFS01','WTQT','FQJCSM','SMQT',
    'FDJC','CHSH','SHC','SW','GJ','JH','HJZHX','CYM','ZHSD','JL','ZHWU','ZG','WFZ','HY','QSH','SHY','GQ','ZHSHCHQ','CK','BDCHWQ','DCHWQ','ZHQ','XNCHQL','CCHQT','SHR',
    'PJJJ','XP','ZHWEN','YJSHY','QMSHY','BKKL','YWSHJ','CHWDAJZ','ZHSHZHZH','WBXF','ZBXZ');

DROP TABLE IF EXISTS `notarization_matters_question`;
CREATE TABLE `notarization_matters_question` (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `notarizationId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公证主表id',
  `question` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题',
  `answer` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回答',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事项类型,notarzation_matters表id',
  `sort` int NOT NULL DEFAULT '0' COMMENT '问题排序',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `updatedBy` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='公证事项问题汇总表';


DROP TABLE IF EXISTS `notarization_matters`;
CREATE TABLE `notarization_matters`  (
 `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
 `code` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事项code',
 `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事项名称',
 `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-无效，1-有效',
 `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `createdBy` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
 `updatedBy` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '公证事项表' ROW_FORMAT = Dynamic;



DROP TABLE IF EXISTS `notarization_matters_special`;
CREATE TABLE `notarization_matters_special` (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `notarizationId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公证主表id',
  `notarizationType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事项类型',
  `entryKey` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '输入项key值',
  `entryValue` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '输入项value值',
  `entryType` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '输入项类型：string，date等',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updatedBy` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='公证事项特殊项汇总表';