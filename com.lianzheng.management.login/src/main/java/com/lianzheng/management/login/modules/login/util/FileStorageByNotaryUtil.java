package com.lianzheng.management.login.modules.login.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileReq;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileRes;
import com.lianzheng.common_service.file_storage_sdk.util.FileStorageUtil;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.auth.mgmt.dao.SysNotarialOfficeDao;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.UUID;

@Component
public class FileStorageByNotaryUtil {
    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Resource
    private SysNotarialOfficeDao sysNotarialOfficeDao;

    public UploadFileRes uploadFile(MultipartFile multipartFile,SysNotarialOfficeEntity sysNotarialOfficeEntity) throws Exception{
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf(".")+1);
        // 用uuid作为文件名，防止生成的临时文件重复
        fileName  = UUID.randomUUID().toString();
//        File file = File.createTempFile(fileName,prefix);
        File file = new File("/"+fileName);
        // MultipartFile to File
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
        if (sysNotarialOfficeEntity == null){
            Long userId = ShiroUtils.getUserId();
            //查询公证处
            sysNotarialOfficeEntity = sysNotarialOfficeDao.findOneByUserId(userId);
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
        UploadFileRes uploadFileRes = fileStorageUtil.uploadFile(uploadFileReq);
        file.delete();
        return uploadFileRes;
    }


    public static String generateToken(String fileName, String secretKey) {
        String content = fileName + "," + System.currentTimeMillis();
        byte[] key = Base64.decode(secretKey);
        AES aes = SecureUtil.aes(key);
        return aes.encryptHex(content);
    }
}
