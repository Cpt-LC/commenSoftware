package com.lianzheng.core.wechat.sessionstore.message.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.lianzheng.core.wechat.db.entity.WechatSession;
import com.lianzheng.core.wechat.db.service.WechatSessionService;
import com.lianzheng.core.wechat.externalcontact.ExternalContractConfig;
import com.lianzheng.core.wechat.externalcontact.ExternalContractInfoUtil;
import com.lianzheng.core.wechat.sessionstore.ChatItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 微信消息处理worker类的基类，包含相应的公共方法
 * @author: 何江雁
 * @date: 2021年10月19日 17:22
 */
public class MessageWorkerBase {
    @Autowired
    WechatSessionService wechatSessionService;
    private ExternalContractConfig externalContractConfig;

    private void initExternalContractConfig(){
        if(externalContractConfig !=null){
            ExternalContractInfoUtil.SetConfig(externalContractConfig);
        }
    }

    public MessageWorkerBase(ExternalContractConfig externalContractConfig){
        this.externalContractConfig = externalContractConfig;
        initExternalContractConfig();
    }

    private String getUnionId(String externalContractId) throws SQLException, IOException {
        String sql = "select unionid from `user` where weworkContractId=@externalContractId";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("externalContractId", externalContractId);

        String unionid = wechatSessionService.getUnionId(externalContractId);
        if(!unionid.isEmpty()){
            return unionid;
        }
        unionid = ExternalContractInfoUtil.getUnionId(externalContractId);
        return unionid;
    }

    /*
    * @Description: 如果msgtype和content的关键字不一样时，则需要相应的worker类重写该值。使得parse4Content可以准确获得content
    * @author: 何江雁
    * @date: 2021/10/22 17:05
    * @param null:
    * @Return:
    */
    protected String overwriteContentType(String msgType){
        return msgType;
    }
    protected String parse4Content(ChatItem data) {
        String msgType = overwriteContentType(data.getMessageType());
        Object content = data.getDecryptedData().get(msgType);
        Assert.notNull(content, String.format("Not found the content (msgType: %s)" , msgType));
        return content.toString();
    }
    @Transactional
    public void storeData(ChatItem data) throws SQLException, IOException {
        WechatSession entity = new WechatSession();

        entity.setMsgid(data.getDecryptedData().getString("msgid"));
        entity.setSeq(data.getSeq());
        entity.setMsgtype(data.getMessageType());
        String from = data.getDecryptedData().get("from").toString();
        entity.setFrom(from);

        String unionid = getUnionId(from);
        entity.setUnionid(unionid);
        entity.setTolist(((JSONArray)data.getDecryptedData().get("tolist")).toJSONString());
        entity.setContent(parse4Content(data));
        entity.setAction(data.getDecryptedData().getString("action"));
        entity.setRoomid(data.getDecryptedData().getString("roomid"));

        entity.setMsgtime(new Date((Long)data.getDecryptedData().get("msgtime")));

        wechatSessionService.insertIgnore(entity);

        wechatSessionService.updateByUnionId(entity);
    }
}
