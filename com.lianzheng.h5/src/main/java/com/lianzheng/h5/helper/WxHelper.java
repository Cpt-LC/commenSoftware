package com.lianzheng.h5.helper;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class WxHelper {
    public static JSONObject  jscode2session(String appid,String secret, String code){
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        String jsonData = restTemplate.getForObject(url, String.class);
        log.info("jsonData>>>{}",jsonData);
        return (JSONObject)JSONObject.parse(jsonData);
    }


    public static JSONObject  access_token(String appid,String secret, String code){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid +
                "&secret=" + secret +
                "&code=" + code +
                "&grant_type=authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        String jsonData = restTemplate.getForObject(url, String.class);
        log.info("jsonData>>>{}",jsonData);
        return (JSONObject)JSONObject.parse(jsonData);
    }
}
