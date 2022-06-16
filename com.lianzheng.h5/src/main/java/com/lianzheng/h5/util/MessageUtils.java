package com.lianzheng.h5.util;

import com.lianzheng.core.server.ResponseBase;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageUtils {
    public static ResponseBase doSendMessage(String template, String signName, String[] phoneNumberSet, Map<String,Object> templateParamMap) {
        Map<String, Object> map = new HashMap<>();
        map.put("template", template);
        map.put("signName", signName);
        map.put("phoneNumberSet", phoneNumberSet);
        map.put("templateParamMap", templateParamMap);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject("http://127.0.0.1:9103/message/message/send", map, ResponseBase.class);
    }
}
