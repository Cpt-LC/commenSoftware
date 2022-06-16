package com.lianzheng.core.auth.mgmt.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公证处管理
 *
 * @author lxk
 **/
@Mapper
@Repository
@DS("mgmt")
public interface SysNotarialOfficeDao extends BaseMapper<SysNotarialOfficeEntity> {

    List<SysNotarialOfficeEntity> selectByName(SysNotarialOfficeEntity sysNotarialOfficeEntity);

    SysNotarialOfficeEntity findOneByUserId(Long userId);

}
