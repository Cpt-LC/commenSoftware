package com.lianzheng.notarization.master.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.notarization.master.entity.NotarizationMattersSpecialEntity;

import java.util.Map;

public interface NotarizationMattersSpecialService extends IService<NotarizationMattersSpecialEntity> {

    /**
     * 获取详情的公共参数
     * @param masterId
     * @return
     */
    Map<String, Object> getInfoMapParam(String masterId);
}
