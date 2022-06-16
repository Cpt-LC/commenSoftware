package com.lianzheng.h5.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.lianzheng.h5.entity.SysNotarialOfficeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公证处管理
 *
 * @author lxk
 **/

@Repository
@DS("mgmt")
public interface SysNotarialOfficeMapper extends BaseMapper<SysNotarialOfficeEntity> {

}
