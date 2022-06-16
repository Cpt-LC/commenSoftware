package com.lianzheng.h5.entity;

import lombok.Data;

@Data
public class AppWechatEntity {
    private String appId;
    private Long expire_time;
    private String nonceStr;
    private String timestamp;
    private String signature;
    private String jsapi_ticket;
    private String access_token;

}
