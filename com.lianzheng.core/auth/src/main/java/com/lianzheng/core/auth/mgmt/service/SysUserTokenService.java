package com.lianzheng.core.auth.mgmt.service;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.core.auth.mgmt.entity.SysUserTokenEntity;

import java.util.Map;


/**
 * 用户Token
 *
 * @author gang.shen@kedata.com
 */
@DS("mgmt")
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

    /**
     * 生成token
     *
     * @param userId 用户ID
     */
    Map<String, Object> createToken(long userId);

    /**
     * 退出，修改token值
     *
     * @param userId 用户ID
     */
    void logout(long userId);

}
