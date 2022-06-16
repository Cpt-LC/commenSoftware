package com.lianzheng.core.wechat.sessionstore.message.worker;

import com.lianzheng.core.wechat.externalcontact.ExternalContractConfig;
import org.springframework.stereotype.Component;

/**
 * @Description: 处理MarkDown格式消息和图文消息的微信会话内容
 * "msgtype":"markdown",
 * "msgtype":"news",
 * "info":{"content":"请前往系统查看，谢谢。"}}
 * @author: 何江雁
 * @date: 2021年10月19日 17:22
 */
@Component
public class InfoMessageWorker extends MessageWorkerBase {
    @Override
    protected String overwriteContentType(String msgType){
        return "info";
    }
    public InfoMessageWorker(ExternalContractConfig externalContractConfig){
        super(externalContractConfig);
    }
}
