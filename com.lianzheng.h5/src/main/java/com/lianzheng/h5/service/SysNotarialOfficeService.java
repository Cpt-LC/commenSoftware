package com.lianzheng.h5.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.h5.entity.SysNotarialOfficeEntity;

import java.util.Map;

/**
 * 公证处管理
 *
 * @author lxk
 **/
@DS("mgmt")
public interface SysNotarialOfficeService extends IService<SysNotarialOfficeEntity> {

}
