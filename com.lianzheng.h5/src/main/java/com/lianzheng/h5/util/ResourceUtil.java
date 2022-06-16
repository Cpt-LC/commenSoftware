package com.lianzheng.h5.util;

import cn.hutool.core.io.FileUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceUtil {

    public static String saveToNewPath(String oldFile){

        String classPath = "";
        try {
            classPath = ResourceUtils.getURL("classpath:").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String newAppCertPath = classPath + File.separator + oldFile;

        if (!new File(newAppCertPath).exists()) {
            String certContent = ResourceUtil.getCertContentByPath(oldFile);
            FileUtil.writeBytes(certContent.getBytes(), newAppCertPath);
        }

        return newAppCertPath;
    }

    public static String getCertContentByPath(String name) {
        InputStream inputStream = null;
        String content = null;
        try {

            ClassPathResource classpathResource = new ClassPathResource(name);
            inputStream = classpathResource.getInputStream();

            content = new String(FileCopyUtils.copyToByteArray(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

}
