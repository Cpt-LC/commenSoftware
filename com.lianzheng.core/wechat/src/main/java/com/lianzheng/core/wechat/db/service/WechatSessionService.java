package com.lianzheng.core.wechat.db.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.core.wechat.db.entity.WechatSession;

import java.util.Map;

/**
* @author 智证hjy
* @description 针对表【wechat_session(存储企业微信的原始会话内容（解密过的）)】的数据库操作Service
* @createDate 2021-12-07 15:46:51
*/
@DS("app")
public interface WechatSessionService extends IService<WechatSession> {

    String getUnionId(String externalContractId);

    void insertIgnore(WechatSession entity);

    void updateByUnionId(WechatSession entity);
}
