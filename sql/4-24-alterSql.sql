

alter table notarzation_master add column companyType  varchar(20) default null comment '企业类型:sys_dict表type为org_type' after legalStatus;
alter table notarzation_master add column registerDate  DATE default null comment '成立日期' after companyType;
ALTER TABLE `order` CHANGE `paymentStatus` `paymentStatus` ENUM ('NotPaid','Paid','Canceled','Refunded');



DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `code` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'code值',
  `label` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标签名',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `parentId` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '父级编号',
  `description` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `delFlag` bit(1) NOT NULL DEFAULT 0 COMMENT '删除标记:0-未删除，1-已删除',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `updatedBy` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统字典表' ROW_FORMAT = Dynamic;




INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('85dfbdc5-c3a7-11ec-b48d-b8cb299dab4c', 'jg', '机关', 'org_type', 0, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('977cb0d5-c3a9-11ec-b48d-b8cb299dab4c', 'shydw', '事业单位', 'org_type', 1, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('9790011e-c3a9-11ec-b48d-b8cb299dab4c', 'shhtt', '社会团体', 'org_type', 2, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('979eb660-c3a9-11ec-b48d-b8cb299dab4c', 'jjh', '基金会', 'org_type', 3, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('97a39a57-c3a9-11ec-b48d-b8cb299dab4c', 'shhjjfw', '社会机构服务', 'org_type', 4, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('97a8de04-c3a9-11ec-b48d-b8cb299dab4c', 'yxzrgs', '有限责任公司', 'org_type', 5, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('97ae2707-c3a9-11ec-b48d-b8cb299dab4c', 'gfyxgs', '股份有限公司', 'org_type', 6, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('97b2e706-c3a9-11ec-b48d-b8cb299dab4c', 'qtqy', '其它企业', 'org_type', 7, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('97b83335-c3a9-11ec-b48d-b8cb299dab4c', 'ncjt', '农村集体', 'org_type', 8, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('cb7b179f-c3a9-11ec-b48d-b8cb299dab4c', 'ncjtjjzzh', '农村集体经济组织', 'org_type', 9, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('cb813590-c3a9-11ec-b48d-b8cb299dab4c', 'chzhnchzzzh', '城镇农村合作组织', 'org_type', 10, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('cb8651e7-c3a9-11ec-b48d-b8cb299dab4c', 'jcqzhxzzhzzh', '基层群众性自治组织', 'org_type', 11, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('cb8caf2f-c3a9-11ec-b48d-b8cb299dab4c', 'dzqy', '独资企业', 'org_type', 12, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('cb96e307-c3a9-11ec-b48d-b8cb299dab4c', 'hhqy', '合伙企业', 'org_type', 13, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('cba11745-c3a9-11ec-b48d-b8cb299dab4c', 'ffrqyfwjg', '非法人企业服务机构', 'org_type', 14, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');
INSERT INTO `online`.`sys_dict`(`id`, `code`, `label`, `type`, `sort`, `parentId`, `description`, `delFlag`, `createdTime`, `updatedTime`, `createdBy`, `updatedBy`) VALUES ('cba78fa8-c3a9-11ec-b48d-b8cb299dab4c', 'qtzzh', '其他组织', 'org_type', 15, NULL, '机构类型', b'0', '2022-04-24 16:21:33', '2022-04-24 16:21:33', '', '');

