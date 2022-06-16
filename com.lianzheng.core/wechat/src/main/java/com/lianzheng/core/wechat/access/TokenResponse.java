package com.lianzheng.core.wechat.access;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Description: TokenResponse的请求返回类
 * @author: 何江雁
 * @date: 2021年10月25日 15:01
 */

public class TokenResponse {

    private int errcode;
    
    public void setErrCode(int errcode){
    	this.errcode = errcode;
    }
    
    public int getErrCode(){
    	return this.errcode;
    }

    private String errmsg;

    public void setErrMsg(String errmsg){
    	this.errmsg = errmsg;
    }

    public String getErrMsg(){
    	return this.errmsg;
    }


    @JSONField(name = "access_token")
    private String token;

    public void setToken(String token){
    	this.token = token;
    }

    public String getToken(){
    	return this.token;
    }

    @JSONField(name = "expires_in")
    private int expires;

    public void setExpires(int expires){
    	this.expires = expires;
    }

    public int getExpires(){
    	return this.expires;
    }
}