package com.lianzheng.message.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author kk
 * @date 2021/12/13 17:39
 * @describe
 * @remark
 */
@Data
public class MessageReqEntity {

    private String template;
    private String signName;
    private String[] phoneNumberSet;
    private String[] templateParamSet;

    private Map<String,Object> templateParamMap;

}
