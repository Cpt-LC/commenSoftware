package com.lianzheng.core.wechat.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianzheng.core.wechat.db.entity.WechatSession;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author 智证hjy
* @description 针对表【wechat_session(存储企业微信的原始会话内容（解密过的）)】的数据库操作Mapper
* @createDate 2021-12-07 16:12:10
* @Entity db..WechatSession
*/
@Mapper
@Repository
public interface WechatSessionMapper extends BaseMapper<WechatSession> {


    String getUnionId(String externalContractId);

    void insertIgnore(WechatSession entity);

    void updateByUnionId(WechatSession entity);

}
