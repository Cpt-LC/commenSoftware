ALTER TABLE `notarzation_master` MODIFY `realName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `notarzation_master` MODIFY `authComment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '公证员审核意见';
ALTER TABLE `notarzation_master` MODIFY `staffRemark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '公证员备注';
ALTER TABLE `notarzation_master` MODIFY `userRemark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '用户备注';
ALTER TABLE `notarzation_master` MODIFY `directorRejectedComment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '主任审核意见';

ALTER TABLE `notarization_matters_special` MODIFY COLUMN entryValue VARCHAR(550) COMMENT '字段值';
ALTER TABLE `notarization_tax` MODIFY `issuingAuthority` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `notarization_degree` MODIFY `issuingAuthority` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `notarization_degree` MODIFY `degreeName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `notarization_driver_license` MODIFY `issuingAuthority` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `notarzation_certificate` MODIFY `notarialCertificateNo` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `user` MODIFY `realName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE `log` MODIFY `requestBody` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

