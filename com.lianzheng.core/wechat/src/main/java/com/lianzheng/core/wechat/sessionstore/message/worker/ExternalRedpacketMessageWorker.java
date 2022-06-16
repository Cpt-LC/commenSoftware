package com.lianzheng.core.wechat.sessionstore.message.worker;

import com.lianzheng.core.wechat.externalcontact.ExternalContractConfig;
import com.lianzheng.core.wechat.sessionstore.ChatItem;
import org.springframework.stereotype.Component;

/**
 * @Description: 处理互通红包消息消息的微信会话内容
 * @author: 何江雁
 * @date: 2021年10月19日 17:22
 */
@Component
public class ExternalRedpacketMessageWorker extends MessageWorkerBase {
    @Override
    protected String overwriteContentType(String msgType){
        return "redpacket";
    }
    public ExternalRedpacketMessageWorker(ExternalContractConfig externalContractConfig){
        super(externalContractConfig);
    }

}
