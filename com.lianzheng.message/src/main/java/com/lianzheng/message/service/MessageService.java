package com.lianzheng.message.service;

import com.lianzheng.core.auth.mgmt.exception.RRException;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.message.entity.MessageReqEntity;

/**
 * @author kk
 * @date 2021/12/13 12:00
 * @describe
 * @remark
 */
public interface MessageService {

    /**
     * 发送短信
     * @param bean
     * @return
     * @throws RRException
     */
    ResponseBase sendMessage(MessageReqEntity bean) throws RRException;

}
