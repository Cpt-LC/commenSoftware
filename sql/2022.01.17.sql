ALTER TABLE `online`.`notarzation_master` 
ADD COLUMN `directorRejectedComment` VARCHAR(256) CHARACTER SET 'utf8' NULL DEFAULT NULL COMMENT '主任拒批备注' AFTER `authComment`;
