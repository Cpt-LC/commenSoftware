package com.lianzheng.notarization.master.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 建行逻辑层
 */
public interface UserNotarzationMasterOneService extends IService<NotarzationMasterEntity> {
    /**
     * 获取建行询问笔录的公共的参数
     * @param notarzationMasterEntity 公证信息
     * @return
     */
    ConcurrentHashMap<String, Map<String,Object>> getPublicParam(NotarzationMasterEntity notarzationMasterEntity);
}
