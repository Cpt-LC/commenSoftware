package com.lianzheng.core.log.curd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_specific_log")
public class SysSpecificLogEntity {

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
    private String mark;
}
