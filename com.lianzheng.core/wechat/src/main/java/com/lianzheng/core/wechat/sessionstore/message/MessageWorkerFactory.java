package com.lianzheng.core.wechat.sessionstore.message;

import com.lianzheng.core.wechat.sessionstore.ChatItem;
import com.lianzheng.core.wechat.sessionstore.message.worker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;

/**
 * @Description: 消息工作者的工厂，可根据不同的消息类型返回
 * @author: 何江雁
 * @date: 2021年10月19日 17:21
 */
@Component
public class MessageWorkerFactory {
    @Autowired
    private ImageMessageWorker imageWorker;
    @Autowired
    private DocMessageWorker docWorker;
    @Autowired
    private InfoMessageWorker infoMessageWorker;
    @Autowired
    private GeneralMessageWorker otherWorker;

    public MessageWorkerBase getWorker(ChatItem data) throws OperationNotSupportedException {
        String messageType = data.getMessageType();
        switch (messageType){
            case "image":
                return imageWorker;
            case "docmsg":
                return docWorker;
            case "markdown":
            case "news":
                return infoMessageWorker;
        }

//        throw new OperationNotSupportedException("Not support the message type:" + messageType);
        return otherWorker;
    }
}
