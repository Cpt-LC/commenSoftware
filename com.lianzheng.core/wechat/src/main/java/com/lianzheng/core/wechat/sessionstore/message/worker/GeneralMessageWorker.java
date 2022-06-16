package com.lianzheng.core.wechat.sessionstore.message.worker;

import com.lianzheng.core.wechat.externalcontact.ExternalContractConfig;
import org.springframework.stereotype.Component;

/**
 * @Description: 处理消息的微信会话内容的通用类
 * "text":{"content":"test"}
 * @author: 何江雁
 * @date: 2021年10月19日 17:22
 */
@Component
public class GeneralMessageWorker extends MessageWorkerBase {
    public GeneralMessageWorker(ExternalContractConfig externalContractConfig){
        super(externalContractConfig);
    }
}
