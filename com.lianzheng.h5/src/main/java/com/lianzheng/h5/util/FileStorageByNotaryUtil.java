package com.lianzheng.h5.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileReq;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileRes;
import com.lianzheng.common_service.file_storage_sdk.util.FileStorageUtil;
import com.lianzheng.h5.entity.SysNotarialOfficeEntity;
import com.lianzheng.h5.jwt.util.JwtUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileStorageByNotaryUtil {
    @Autowired
    private FileStorageUtil fileStorageUtil;

    public UploadFileRes uploadFile(File file){

        SysNotarialOfficeEntity sysNotarialOfficeEntity = JwtUtils.getSysNotarialOffice();
        String secretKey = sysNotarialOfficeEntity.getSecretKey();
        String baseUrl = sysNotarialOfficeEntity.getBaseUrl();
        if(StringUtils.isEmpty(secretKey)||StringUtils.isEmpty(baseUrl)){
            throw new RuntimeException("请补全公证处密钥或域名信息");
        }

        String fileName = file.getName();
        String prefix =  fileName.substring(fileName.lastIndexOf(".")+1);
        UploadFileReq uploadFileReq = new UploadFileReq();
        uploadFileReq.setFile(file);
        uploadFileReq.setFileExt(prefix);
        uploadFileReq.setFileName(fileName);
        uploadFileReq.setSecretKey(secretKey);
        uploadFileReq.setUrl(baseUrl);
        UploadFileRes uploadFileRes = fileStorageUtil.uploadFile(uploadFileReq);
        return  uploadFileRes;
    }


    public static String generateToken(String fileName, String secretKey) {
        String content = fileName + "," + System.currentTimeMillis();
        byte[] key = Base64.decode(secretKey);
        AES aes = SecureUtil.aes(key);
        return aes.encryptHex(content);
    }
}
