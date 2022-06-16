package com.lianzheng.h5.helper;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.lianzheng.h5.form.IdentifyForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Slf4j
public class UserInfoHelper {


    public static JSONObject getInfo(String code,IdentifyForm identifyForm) {
        String baseUrl = identifyForm.getBaseUrl();

        String requestUrl = baseUrl + "/userauth/oauth/token";
        ///获取授权令牌 token 接口
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("client_id", identifyForm.getClientId());
        paramMap.put("client_secret", identifyForm.getClientSecret());
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("code", code);
        HttpServletRequest request = getRequest();
        String redirect_uri = identifyForm.getRedirectUri();

        paramMap.put("redirect_uri", redirect_uri);
        String response = HttpUtil.post(requestUrl, paramMap);
        log.info(response);

        String tokenValue = JSONObject.parseObject(response).getString("value");

        /// 获取用户 ID接口
        requestUrl = baseUrl + "/cityuserinfo/user/open/getUserId";
        response = HttpRequest.post(requestUrl)
                .header("Authorization", "Bearer " + tokenValue)
                .timeout(20000)//超时，毫秒
                .execute().body();

        String userIdValue = JSONObject.parseObject(response).getString("data");
        log.info(userIdValue);

        ///获取用户信息接口
        requestUrl = baseUrl + "/cityuserinfo/user/open/getUserInfo";
        HashMap<String, Object> userInfoJsonParams = new HashMap<String, Object>();
        userInfoJsonParams.put("userId", userIdValue);
        response = HttpRequest.post(requestUrl)
                .header("Authorization", "Bearer " + tokenValue)
                .form(userInfoJsonParams)
                .timeout(20000)//超时，毫秒
                .execute().body();
        log.info(response);

        return JSONObject.parseObject(response);
    }


    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }
}
