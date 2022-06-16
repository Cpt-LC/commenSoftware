package com.lianzheng.core.payment.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Author: demo
 * @Date: 2020/9/23 15:51
 */
@Data
@Slf4j
public class WxPayProperties implements WXPayConfig {
    /**
     * appid.
     */
    private byte[] certData;


//    @Value("${wxpay.appId}")
    private String appId;

    /**
     * 微信支付商户号.
     */
//    @Value("${wxpay.mchId}")
    private String mchId;

    /**
     * 微信支付商户密钥.
     */
//    @Value("${wxpay.mchKey}")
    private String mchKey;

    /**
     * 异步回调地址
     */
//    @Value("${wxpay.notifyUrl}")
    private String notifyUrl;

//    @Value("${wxpay.time_expire}")
    private String timeExpire;

    private String openId;

    public String getAppID() {
        return this.appId;
    }

    public String getMchID() {
        return this.mchId;
    }

    public String getKey() {
        return this.mchKey;
    }

    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }




}


