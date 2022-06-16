package com.lianzheng.core.log.curd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("log")
public class LogEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date logTime;
    private String url;
    private String message;
    private String requestId;
    private Integer duration;
    private Integer level;
    private String method;
    private String serviceName;
    private Integer pid;
    private String hostname;
    private Integer statusCode;
    private String clientIp;
    private String referer;
    private String requestHeaders;
    private String requestParams;
    private String requestBody;
    private String responseHeaders;
    private String responseBody;

    @Override
    public String toString() {
        return "LogEntity{" +
                "id=" + id +
                ", logTime=" + logTime +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", requestId='" + requestId + '\'' +
                ", duration=" + duration +
                ", level=" + level +
                ", method='" + method + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", pid=" + pid +
                ", hostname='" + hostname + '\'' +
                ", statusCode=" + statusCode +
                ", clientIp='" + clientIp + '\'' +
                ", referer='" + referer + '\'' +
                ", requestHeaders='" + requestHeaders + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", responseHeaders='" + responseHeaders + '\'' +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
