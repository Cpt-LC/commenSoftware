package com.lianzheng.core.request;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2021年10月25日 15:25
 */
public class RequestInput {
    public RequestInput(String method, String url, Map<String, String> body){
        this.setMethod(method);
        this.setUrl(url);
        this.setBody(body);
    }
    private String url;

    public void setUrl(String url){
    	this.url = url;
    }

    public String getUrl(){
    	return this.url;
    }

    private String method;

    public void setMethod(String method){
    	this.method = method;
    }

    public String getMethod(){
    	return this.method;
    }
    private int timeout;

    public void setTimeout(int timeout){
    	this.timeout = timeout;
    }

    public int getTimeout(){
    	return this.timeout;
    }

    private Map<String, String> body = new HashMap<>();

    public void setBody(Map<String, String> body){
        if(body == null){
            return;
        }
    	body.forEach((key, value) -> this.body.merge(key,value, (v1, v2)-> v1));
    }

    public Map<String, String> getBody(){
    	return this.body;
    }

    private Map<String, String> headers = new HashMap<>();

    public void setHeaders(Map<String, String> headers){
        if(headers == null){
            return;
        }
        headers.forEach((key, value) -> this.headers.merge(key,value, (v1, v2)-> v1));
    }

    public Map<String, String> getHeaders(){
        return this.headers;
    }
}
