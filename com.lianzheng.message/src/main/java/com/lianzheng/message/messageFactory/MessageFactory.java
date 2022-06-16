package com.lianzheng.message.messageFactory;

import com.lianzheng.message.service.MessageService;
import com.lianzheng.message.service.impl.TencentMessage;

/**
 * @author kk
 * @date 2021/12/13 15:06
 * @describe
 * @remark
 */
public class MessageFactory {

    //获取发送短信的对象
    public MessageService getMessageType(String type){
        if(type == null){
            return null;
        }
        if(type.equalsIgnoreCase("tencent")){
            return new TencentMessage();
        } else if(type.equalsIgnoreCase("yunpian")){
            return null;
        }
        return null;
    }

}
