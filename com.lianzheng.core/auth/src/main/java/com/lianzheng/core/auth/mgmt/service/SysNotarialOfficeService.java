package com.lianzheng.core.auth.mgmt.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;

import java.util.Map;

/**
 * 公证处管理
 *
 * @author lxk
 **/
@DS("mgmt")
public interface SysNotarialOfficeService extends IService<SysNotarialOfficeEntity> {

    Map<String,Object> findList(SysNotarialOfficeEntity sysNotarialOfficeEntity);

    void insert(SysNotarialOfficeEntity sysNotarialOfficeEntity);

    void update(SysNotarialOfficeEntity sysNotarialOfficeEntity);
}
