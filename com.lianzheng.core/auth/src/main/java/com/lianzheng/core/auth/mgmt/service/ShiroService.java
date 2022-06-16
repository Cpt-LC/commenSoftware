package com.lianzheng.core.auth.mgmt.service;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.entity.SysUserTokenEntity;

import java.util.Set;

/**
 * shiro相关接口
 *
 * @author gang.shen@kedata.com
 */
@DS("mgmt")
public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(long userId);

    SysUserTokenEntity queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     *
     * @param userId
     */
    SysUserEntity queryUser(Long userId);
}
