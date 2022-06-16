package com.lianzheng.core.wechat.sessionstore;

import com.alibaba.fastjson.JSONObject;

import javax.naming.OperationNotSupportedException;

/**
 * @Description: 会话消息的实体类
 * @author: 何江雁
 * @date: 2021年10月19日 14:15
 */
public class ChatItem {
    private JSONObject rawData;
    private JSONObject decryptedData;

    public JSONObject getRawData(){
        return this.rawData;
    }

    public JSONObject getDecryptedData(){
        return this.decryptedData;
    }


    public ChatItem(JSONObject rawData, JSONObject decryptedData) throws OperationNotSupportedException {
        this.rawData = rawData;
        this.decryptedData = decryptedData;
    }

    public String getMessageType(){
        return this.getDecryptedData().getString("msgtype");
    }
    public long getSeq(){
        return this.getRawData().getLong("seq");
    }

    @Override
    public String toString(){
        return String.format("{" +
                "    rawData:" + this.rawData.toString() +",\n" +
                "    decryptedData:" + this.decryptedData.toString() +",\n" +
                "}");
    }
}
