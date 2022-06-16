
alter table user add idCardAddress VARCHAR(200) COMMENT '证件地址' AFTER idCardNo;
ALTER TABLE notarzation_master add `isRepeat` int DEFAULT 0 not null  COMMENT '是否重复';
ALTER TABLE notarzation_master ADD email VARCHAR(100) COMMENT '邮箱' AFTER realName;
ALTER TABLE notarzation_master ADD pinyin VARCHAR(50) COMMENT '姓名拼音' AFTER realName;

ALTER TABLE `order` ADD billingName VARCHAR(100) COMMENT '开票名称' AFTER isDeleted;
ALTER TABLE `order` ADD invoiceTaxNo VARCHAR(100) COMMENT '开票税号' AFTER isDeleted;
ALTER TABLE `notarzation_certificate` ADD notarialCertificateNo VARCHAR(45) Not NULL COMMENT '公证编号' AFTER businessId;
ALTER TABLE `notarzation_master` CHANGE `notarzationTypeCode` `notarzationTypeCode` ENUM ('GR', 'TAX', 'DL', 'DC', 'NC');
ALTER TABLE `notarzation_master` drop COLUMN hasForeign;

DROP TABLE IF EXISTS `notarization_degree`;
CREATE TABLE `notarization_degree` (
  `id` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `grantTime` date NOT NULL COMMENT '授予时间',
  `issuingAuthority` varchar(50) NOT NULL COMMENT '授予机构',
  `degreeName` varchar(50) NOT NULL COMMENT '学位名称',
  `isDeleted` tinyint(4) NOT NULL DEFAULT '0',
  `createdTime` datetime NOT NULL,
  `updatedTime` datetime NOT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  `updatedBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_notarzation_master_dc` FOREIGN KEY (`id`) REFERENCES `notarzation_master` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学位证明详情表余表';

DROP TABLE IF EXISTS `notarization_driver_license`;
CREATE TABLE `notarization_driver_license` (
  `id` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `issuingTime` date NOT NULL COMMENT '出具时间',
  `issuingAuthority` varchar(50) NOT NULL COMMENT '颁发机构',
  `isDeleted` tinyint(4) NOT NULL DEFAULT '0',
  `createdTime` datetime NOT NULL,
  `updatedTime` datetime NOT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  `updatedBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_notarzation_master_dl` FOREIGN KEY (`id`) REFERENCES `notarzation_master` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='驾驶证明详情表余表';


DROP TABLE IF EXISTS `notarization_tax`;
CREATE TABLE `notarization_tax` (
  `id` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `issuingTime` date NOT NULL COMMENT '授予时间',
  `issuingAuthority` varchar(50) NOT NULL COMMENT '颁发机构',
  `isDeleted` tinyint(4) NOT NULL DEFAULT '0',
  `createdTime` datetime NOT NULL,
  `updatedTime` datetime NOT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  `updatedBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_notarzation_master_tax` FOREIGN KEY (`id`) REFERENCES `notarzation_master` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='完税证明详情表余表';



ALTER TABLE `online`.`notarzation_graduation`
CHANGE COLUMN `updatedBy` `updatedBy` VARCHAR(45) NOT NULL ;
ALTER TABLE `online`.`notarization_degree`
CHANGE COLUMN `createdBy` `createdBy` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `updatedBy` `updatedBy` VARCHAR(45) NOT NULL ;
ALTER TABLE `online`.`notarization_driver_license`
CHANGE COLUMN `createdBy` `createdBy` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `updatedBy` `updatedBy` VARCHAR(45) NOT NULL ;
ALTER TABLE `online`.`notarization_tax`
CHANGE COLUMN `createdBy` `createdBy` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `updatedBy` `updatedBy` VARCHAR(45) NOT NULL ;
ALTER TABLE `online`.`order`
CHANGE COLUMN `createdBy` `createdBy` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `updatedBy` `updatedBy` VARCHAR(45) NOT NULL ;
ALTER TABLE notarzation_master MODIFY COLUMN idCardAddress VARCHAR(200) COMMENT '身份证地址。如果证件类型是身份证的时候，该值不为空';