package com.lianzheng.core.server;


import com.lianzheng.core.server.auth.TokenInfo;
import org.springframework.lang.Nullable;

/**
 * @Description: 请求头的实体类，包含token解析后的当前用户信息
 * @author: 何江雁
 * @date: 2021年10月18日 17:47
 */
public class HeaderInfo {
    public HeaderInfo(){

    }
    public HeaderInfo(String appid, @Nullable String requestId, @Nullable TokenInfo tokenInfo){
        setAppid(appid);
        setRequestId(requestId);
        setTokenInfo(tokenInfo);

    }
    private String appid;

    public void setAppid(String appid){
        this.appid = appid;
    }

    public String getAppid(){
        return this.appid;
    }

    private TokenInfo tokenInfo;

    public void setTokenInfo(TokenInfo tokenInfo){
        this.tokenInfo = tokenInfo;
    }

    public TokenInfo getTokenInfo(){
        return this.tokenInfo;
    }

    private String requestId;

    public void setRequestId(String requestId){
        this.requestId = requestId;
    }

    public String getRequestId(){
        return this.requestId;
    }

    @Override
    public String toString(){
        return String.format("{"  + ",\n" +
                "    appid:" + getAppid() + ",\n" +
                "    requestId:" + getRequestId() + ",\n" +
                "    TokenInfo:" + getTokenInfo().toString() + "\n" +
                "}");
    }
}
