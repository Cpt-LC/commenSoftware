package com.lianzheng.core.server.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Description: UserInfo的请求参数类
 * @author: 何江雁
 * @date: 2021年10月18日 17:14
 */


public class TokenInfo {
    @JsonProperty("version")
    private String version;

    public void setVersion(String version){
    	this.version = version;
    }

    public String getVersion(){
    	return this.version;
    }

    @JsonProperty("userId")
    private String userId;

    public void setUserId(String userId){
    	this.userId = userId;
    }

    public String getUserId(){
    	return this.userId;
    }

    @JsonProperty("openid")
    private String openid;

    public void setOpenId(String openid){
    	this.openid = openid;
    }

    public String getOpenId(){
    	return this.openid;
    }

    @JsonProperty("isMina")
    private Boolean isMina;

    public void setIsMina(Boolean isMina){
    	this.isMina = isMina;
    }

    public Boolean getIsMina(){
    	return this.isMina;
    }
    @Override
    public String toString(){
        return String.format("{"  + ",\n" +
                "    userId:" + getUserId()  + ",\n" +
                "    openid:" + getOpenId()  + ",\n" +
                "    version:" + getVersion()  + ",\n" +
                "    isMina:" + getIsMina().toString()  + "\n" +
                "}");
    }
}