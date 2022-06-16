package com.lianzheng.notarization.master.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;

import java.util.Map;

/**
 * api专用service
 */
public interface UserNotarzationMasterApiService extends IService<NotarzationMasterEntity> {

    Map<String,Object> getMasterInfo(Map<String,Object> param);
}
