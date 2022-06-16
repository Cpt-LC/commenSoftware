package com.lianzheng.core.auth.mgmt.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统用户
 *
 * @author gang.shen@kedata.com
 */
@Mapper
@Repository
public interface SysUserDao extends BaseMapper<SysUserEntity> {

    /**
     * 查询用户的所有权限
     *
     * @param userId 用户ID
     */
    List<String> queryAllPerms(Long userId);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(Long userId);

    /**
     * 根据用户名，查询系统用户
     */
    SysUserEntity queryByUserName(String username);
    SysUserEntity queryByUserId(Long id);
    List<SysUserEntity> getUser(@Param("roleName") String roleName,@Param("notarialOfficeId") Long notarialOfficeId);

    /**
     * 查询用户roleId
     */
    Long getUserRoleId(Long UserId);

}
