package com.lianzheng.core.wechat.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.wechat.db.dao.WechatSessionMapper;
import com.lianzheng.core.wechat.db.entity.WechatSession;
import com.lianzheng.core.wechat.db.service.WechatSessionService;
import org.springframework.stereotype.Service;

/**
* @author 何江雁
* @description 针对表【wechat_session(存储企业微信的原始会话内容（解密过的）)】的数据库操作Service实现
* @createDate 2021-12-07 16:12:10
*/
@Service
public class WechatSessionServiceImpl extends ServiceImpl<WechatSessionMapper, WechatSession>
implements WechatSessionService {
    @Override
    public String getUnionId(String externalContractId) {
        return baseMapper.getUnionId(externalContractId);
    }

    @Override
    public void insertIgnore(WechatSession entity) {
        baseMapper.insertIgnore(entity);
    }

    @Override
    public void updateByUnionId(WechatSession entity) {
        baseMapper.updateByUnionId(entity);
    }
}
