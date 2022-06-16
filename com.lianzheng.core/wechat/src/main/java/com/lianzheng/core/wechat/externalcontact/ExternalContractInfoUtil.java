package com.lianzheng.core.wechat.externalcontact;

import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.request.HttpClient;
import com.lianzheng.core.wechat.access.TokenResponse;
import com.lianzheng.core.wechat.access.TokenUtil;
import com.lianzheng.core.wechat.sessionstore.WechatSessionConfig;
import org.apache.http.util.Asserts;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Description: 企业微信对外部用户信息的操作工具类
 * @author: 何江雁
 * @date: 2021年10月25日 17:47
 */
@Component
public class ExternalContractInfoUtil {
    private static ExternalContractConfig _config = null;
    public static void SetConfig(ExternalContractConfig config){
        _config = config;
    }

    private static final String URL_TEMPLATE = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get?access_token=%s&external_userid=%s&cursor=";

    public static final JSONObject getDetail(String externalUserID) throws IOException {
        Asserts.check(_config != null, "Please set the config before ");
        TokenResponse response = TokenUtil.getAccessToken(_config.getCorpid(), _config.getSecret());

        JSONObject result =HttpClient.get(String.format(URL_TEMPLATE,response.getToken(), externalUserID, ""), new HashMap<>());

        Asserts.notNull(result, "getDetail result");
        Asserts.check((int)result.get("errcode") == 0, response.getErrMsg());
        return (JSONObject)result.get("external_contact");
    }
    public static final String getUnionId(String externalUserID) throws IOException {
        JSONObject result = getDetail(externalUserID);
        Asserts.check(result.containsKey("unionid"), "No unionid");
        return result.get("unionid").toString();
    }
}
