package com.lianzheng.core.wechat.access;

/**
 * @Description: 获取企业微信access token工具类
 * @author: 何江雁
 * @date: 2021年10月25日 14:42
 */
import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.request.HttpClient;
import org.apache.http.util.Asserts;

import java.io.IOException;
import java.util.HashMap;

public class TokenUtil {
    private static final String URL_TEMPLATE = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    public static final TokenResponse getAccessToken(String corpid, String secret) throws IOException {
        //todo:缓存
        TokenResponse response = innerGetAccessToken(corpid, secret);
        Asserts.notNull(response, "token response");
        Asserts.notBlank(response.getToken(), "token");
        Asserts.check(response.getErrCode() == 0, response.getErrMsg());
        return response;
    }
    private static final TokenResponse innerGetAccessToken(String corpid, String secret) throws IOException {
        return HttpClient.post(String.format(URL_TEMPLATE, corpid,secret), new HashMap<>(), TokenResponse.class);
    }
}
