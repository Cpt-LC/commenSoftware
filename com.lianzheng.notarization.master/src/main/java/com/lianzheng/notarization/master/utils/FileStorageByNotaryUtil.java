package com.lianzheng.notarization.master.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileReq;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileRes;
import com.lianzheng.common_service.file_storage_sdk.util.FileStorageUtil;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.auth.mgmt.dao.SysNotarialOfficeDao;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileStorageByNotaryUtil {
    @Resource
    private FileStorageUtil fileStorageUtil;

    @Resource
    private SysNotarialOfficeDao sysNotarialOfficeDao;

    public UploadFileRes uploadFile(File file,Long officeId){
        // 获取文件名
        String fileName = file.getName();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf(".")+1);
            //查询公证处
        SysNotarialOfficeEntity sysNotarialOfficeEntity = sysNotarialOfficeDao.selectById(officeId);
        if(Objects.isNull(sysNotarialOfficeEntity)){
            throw new COREException("该账户不可办理业务");
        }

        String secretKey = sysNotarialOfficeEntity.getSecretKey();
        String baseUrl = sysNotarialOfficeEntity.getBaseUrl();
        if(StringUtils.isEmpty(secretKey)||StringUtils.isEmpty(baseUrl)){
            throw new RuntimeException("请补全公证处密钥或域名信息");
        }
        UploadFileReq uploadFileReq = new UploadFileReq();
        uploadFileReq.setFile(file);
        uploadFileReq.setFileExt(prefix);
        uploadFileReq.setFileName(fileName);
        uploadFileReq.setSecretKey(secretKey);
        uploadFileReq.setUrl(baseUrl);
        return fileStorageUtil.uploadFile(uploadFileReq);
    }


    public static String generateToken(String fileName, String secretKey) {
        String content = fileName + "," + System.currentTimeMillis();
        byte[] key = Base64.decode(secretKey);
        AES aes = SecureUtil.aes(key);
        return aes.encryptHex(content);
    }
}
