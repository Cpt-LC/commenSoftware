package com.lianzheng.h5.form;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Data
@Component
public class IdentifyForm {

    @Value("${LLTidentify.clientId}")
    private  String clientId;
    @Value("${LLTidentify.clientSecret}")
    private String clientSecret;
    @Value("${LLTidentify.baseUrl}")
    private String baseUrl;
    @Value("${LLTidentify.redirectUri}")
    private String redirectUri;

}
