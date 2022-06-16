package com.lianzheng.h5.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;

import com.lianzheng.h5.entity.AppWechatEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于微信登陆
 */
@Slf4j
@Component
public class WxUtils {
    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.secret}")
    private String secret;
    @Value("${outConfigPath}")
    private String tokenpath;
    /**
     * 生成signature
     **/
    public AppWechatEntity sign(String url) {
        Map<String, String> ret = new HashMap();
        String nonceStr = create_nonce_str();
        String timestamp = Long.toString(create_timestamp());
        String string1;
        String signature = "";
        //获取jsapi_ticket和过期时间
        AppWechatEntity appWechatEntity = getJsapiTicket();
        String jsapiTicket = appWechatEntity.getJsapi_ticket();
        Long expireTime = appWechatEntity.getExpire_time();
        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapiTicket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes(StandardCharsets.UTF_8));
            signature = byteToHex(crypt.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }

        appWechatEntity.setAppId(appid);
        appWechatEntity.setExpire_time(expireTime);
        appWechatEntity.setNonceStr(nonceStr);
        appWechatEntity.setTimestamp(timestamp);
        appWechatEntity.setSignature(signature);
        return appWechatEntity;
    }


    /**
     * 获取jsapi_ticket
     **/
    public AppWechatEntity getJsapiTicket() {
        //logger.debug("--------------开始执行getJsapiTicket方法--------------");
        //定义过期时间
        AppWechatEntity appWechatEntity = new AppWechatEntity();
        String accessTokenString = "";
        String jsapiTicketString = "";
        String jsapi_ticket = "";
        String access_token = "";
        jsapiTicketString = readWechatTokenFile(getJsapiTicketFilePath());
        if (!StringUtils.isEmpty(jsapiTicketString)) {
            appWechatEntity = JSONObject.parseObject(jsapiTicketString, AppWechatEntity.class);
            long expireTime = appWechatEntity.getExpire_time();
            long curTime = create_timestamp();
            if (expireTime >= curTime && StrUtil.isNotEmpty(appWechatEntity.getJsapi_ticket())) {
                //logger.debug("已有的jsapi_ticket=" + jsapi_ticket);
                return appWechatEntity;
            }
        }

        long timestamp = create_timestamp() + 7000;//过期时间是2小时(7200s)
        access_token =
                getAccessTokenData("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret);
        Map accessTokenMap = new HashMap();
        accessTokenMap.put("expire_time", timestamp);
        accessTokenMap.put("access_token", access_token);
        accessTokenString = JSONObject.toJSONString(accessTokenMap);

        jsapi_ticket = getJsapiTicketData("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi");
        Map jsapiTicketMap = new HashMap();
        jsapiTicketMap.put("expire_time", timestamp);
        jsapiTicketMap.put("jsapi_ticket", jsapi_ticket);
        jsapiTicketString = JSONObject.toJSONString(jsapiTicketMap);

        // 写文件
        try {
            FileUtils.writeStringToFile(new File(getAccessTokenFilePath()), accessTokenString, CharsetUtil.CHARSET_UTF_8);
            FileUtils.writeStringToFile(new File(getJsapiTicketFilePath()), jsapiTicketString, CharsetUtil.CHARSET_UTF_8);
            //logger.debug("写入文件成功");
        } catch (IOException e) {
            log.debug("写文件异常：" + e.getMessage());
            e.printStackTrace();
        }

        appWechatEntity.setJsapi_ticket(jsapi_ticket);
        appWechatEntity.setExpire_time(timestamp);
        appWechatEntity.setAccess_token(access_token);
        //logger.debug("--------------结束执行getJsapiTicket方法--------------");
        return appWechatEntity;
    }

    public String getAccessToken() {
        //logger.debug("--------------开始执行getAccessToken方法--------------");
        String access_token = "";
        String accessTokenString = "";
        AppWechatEntity appWechatEntity = new AppWechatEntity();
        accessTokenString = readWechatTokenFile(getAccessTokenFilePath());
        if (StringUtils.isNotEmpty(accessTokenString)) {
            appWechatEntity = JSONObject.parseObject(accessTokenString, AppWechatEntity.class);
            access_token = appWechatEntity.getAccess_token();
            long expireTime = appWechatEntity.getExpire_time();
            long curTime = create_timestamp();
            if (expireTime >= curTime) {
                //logger.debug("已有的access_token=" + access_token);
                return access_token;
            }
        }

        long timestamp = create_timestamp() + 6000;//过期时间是2小时，但是可以提前进行更新，防止前端正好过期
        access_token =
                getAccessTokenData("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret);
        Map accessTokenMap = new HashMap();
        accessTokenMap.put("expire_time", timestamp);
        accessTokenMap.put("access_token", access_token);
        accessTokenString = JSONObject.toJSONString(accessTokenMap);

        // 写文件
        try {
            FileUtils.writeStringToFile(new File(getAccessTokenFilePath()), accessTokenString, CharsetUtil.CHARSET_UTF_8);
            //logger.debug("写入文件成功");
        } catch (IOException e) {
            log.debug("写文件异常：" + e.getMessage());
            e.printStackTrace();
        }
        //logger.debug("新的access_token=" + access_token);
        //logger.debug("--------------结束执行getAccessToken方法--------------");
        return access_token;
    }

    private String readWechatTokenFile(String filePath) {
        String content = "";
        try {
            if (new File(filePath).exists()) {
                FileReader fileReader = new FileReader(filePath, CharsetUtil.CHARSET_UTF_8);
                content = fileReader.readString();
            } else {
                new File(filePath).createNewFile();
            }
        } catch (IOException e) {
            log.error("读文件异常：" + e.getMessage());
            e.printStackTrace();
        }
        return content;
    }

    private String getAccessTokenData(String url) {
        String str = "";
        String result = HttpUtil.get(url, CharsetUtil.CHARSET_UTF_8);
        if (StringUtils.isBlank(result))
            return str;
        str = parseData("access_token", "expires_in", result);
        return str;
    }

    private String getJsapiTicketData(String url) {
        String str = "";
        String result = HttpUtil.get(url, CharsetUtil.CHARSET_UTF_8);
        if (StringUtils.isBlank(result))
            return str;
        str = parseData("ticket", "expires_in", result);
        return str;
    }

    private String parseData(String tokenName, String expiresInName, String data) {
        String tokenConent = "";
        JSONObject jsonObject = JSONObject.parseObject(data);
        try {
            tokenConent = jsonObject.get(tokenName).toString();
            if (StringUtils.isEmpty(tokenConent)) {
                log.error("token获取失败,获取结果" + data);
                return tokenConent;
            }
        } catch (Exception e) {
            log.error("token 结果解析失败，token参数名称: " + tokenName + "有效期参数名称:" + expiresInName + "token请求结果:" + data);
            e.printStackTrace();
            return tokenConent;
        }
        return tokenConent;
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private String create_nonce_str() {
        return IdUtil.simpleUUID();
    }

    private Long create_timestamp() {
        return System.currentTimeMillis() / 1000;
    }


    private String getJsapiTicketFilePath() {
        return tokenpath  + appid + "_jsapiTicket.txt";
    }

    private String getAccessTokenFilePath() {
        return tokenpath  + appid + "_accessToken.txt";
    }


}
