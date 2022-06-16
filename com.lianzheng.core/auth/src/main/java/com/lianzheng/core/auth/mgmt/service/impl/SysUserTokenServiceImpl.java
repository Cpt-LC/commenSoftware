/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.icredit.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lianzheng.core.auth.mgmt.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.auth.mgmt.dao.SysUserTokenDao;
import com.lianzheng.core.auth.mgmt.entity.SysUserTokenEntity;
import com.lianzheng.core.auth.mgmt.oauth2.TokenGenerator;
import com.lianzheng.core.auth.mgmt.service.SysUserTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
    //12小时后过期
    private final static int EXPIRE = 3600 * 12;


    @Override
    public Map<String, Object> createToken(long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

        //判断是否生成过token
        SysUserTokenEntity tokenEntity = this.getById(userId);
        if (tokenEntity == null) {
            tokenEntity = new SysUserTokenEntity();
            tokenEntity.setUserId(userId);
            tokenEntity.setToken(token);
            tokenEntity.setUpdatedTime(now);
            tokenEntity.setExpiredTime(expireTime);

            //保存token
            this.save(tokenEntity);
        } else {
            tokenEntity.setToken(token);
            tokenEntity.setUpdatedTime(now);
            tokenEntity.setExpiredTime(expireTime);

            //更新token
            this.updateById(tokenEntity);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("expire", EXPIRE);

        return result;
    }

    @Override
    public void logout(long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //修改token
        SysUserTokenEntity tokenEntity = new SysUserTokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(token);
        this.updateById(tokenEntity);
    }
}
