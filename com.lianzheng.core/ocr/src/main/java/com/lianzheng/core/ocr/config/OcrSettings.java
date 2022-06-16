package com.lianzheng.core.ocr.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ocr 配置实体类
 * @author: 何江雁
 * @date: 2022年02月16日 17:30
 */
@Data
public class OcrSettings {
    private String type;

    public void setType(String type){
    	this.type = type;
    }

    public String getType(){
    	return this.type;
    }

    private String appid;

    public void setAppId(String appid){
    	this.appid = appid;
    }

    public String getAppId(){
    	return this.appid;
    }

    private String secret;

    public void setSecret(String secret){
    	this.secret = secret;
    }

    public String getSecret(){
    	return this.secret;
    }
}
