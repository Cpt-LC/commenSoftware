package com.lianzheng.core.auth.mgmt.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.UUID;

@Component
public class FileTokenUtils {
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 创建权限
     */
    public String  fileToken(String filename){
        String uuid = UUID.randomUUID().toString();
        String md5 = DigestUtils.md5DigestAsHex(filename.getBytes());
        redisUtils.set(uuid, md5,7200);
        return  uuid;
    }
}
