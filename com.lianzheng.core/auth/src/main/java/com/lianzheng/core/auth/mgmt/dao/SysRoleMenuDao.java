package com.lianzheng.core.auth.mgmt.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.core.auth.mgmt.entity.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色与菜单对应关系
 *
 * @author gang.shen@kedata.com
 */
@Mapper
@Repository
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenuEntity> {

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> queryMenuIdList(Long roleId);

    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(Long[] roleIds);
}
