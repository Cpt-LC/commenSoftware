package com.lianzheng.h5.pay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.jfinal.kit.StrKit;
import com.lianzheng.h5.util.GenerateStrUtil;
import com.lianzheng.core.payment.Result.ResultPay;
import com.lianzheng.core.payment.config.WxPayProperties;
import com.lianzheng.core.payment.kit.IpKit;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WxPayHelper {
    public static ResultPay<Map<String, String>> createdPay(HttpServletRequest req, String orderNo, String amount, String body, String type, Object param) throws Exception {
        WxPayProperties wxPayProperties = (WxPayProperties) param;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(10, Integer.parseInt(wxPayProperties.getTimeExpire()));
        Date exTime = cal.getTime();
        String expireTime = format.format(exTime);
        Map<String, String> returnMap = new HashMap();
        Map<String, String> responseMap = new HashMap();
        String ip = IpKit.getRealIp(req);
        if (StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }

        WXPay wxpay = new WXPay(wxPayProperties);
        Map<String, String> data = new HashMap();
        data.put("body", body);
        data.put("out_trade_no", orderNo);
        data.put("total_fee", amount);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("spbill_create_ip", ip);
        data.put("notify_url", wxPayProperties.getNotifyUrl());
        data.put("time_expire", expireTime);
        data.put("trade_type", "MWEB");

        Map<String, String> resultMap = wxpay.unifiedOrder(data);
        String returnCode = (String) resultMap.get("return_code");
        String returnMsg = (String) resultMap.get("return_msg");
        String timestamp;
        if ("SUCCESS".equals(returnCode)) {
            String resultCode = (String) resultMap.get("result_code");
            timestamp = (String) resultMap.get("err_code_des");
            if ("SUCCESS".equals(resultCode)) {
                responseMap = resultMap;
            }
        }

        if (responseMap != null && !((Map) responseMap).isEmpty()) {
            Long time = System.currentTimeMillis() / 1000L;
            timestamp = time.toString();
            returnMap.put("mweb_url", responseMap.get("mweb_url"));
            returnMap.put("appId", wxPayProperties.getAppID());
            returnMap.put("timeStamp", timestamp);
            returnMap.put("nonceStr", (responseMap).get("nonce_str"));
            returnMap.put("package", "prepay_id=" + (String) ((Map) responseMap).get("prepay_id"));
            returnMap.put("signType", "MD5");
            if (((Map) responseMap).get("code_url") != null) {
                returnMap.put("code_url", (String) ((Map) responseMap).get("code_url"));
            }

            returnMap.put("package", "prepay_id=" + (String) ((Map) responseMap).get("prepay_id"));
            returnMap.put("sign", WXPayUtil.generateSignature(returnMap, wxPayProperties.getKey()));
            returnMap.put("return_code", resultMap.get("return_code"));
            returnMap.put("result_code", resultMap.get("result_code"));
            return new ResultPay(true, "发送微信支付请求成功", 0, returnMap);
        } else {
            return new ResultPay(false, "发送微信支付请求失败", -1, resultMap);
        }
    }

    public static String generateOrderNo() {
//        订单编号：<前缀两位>+年月日8位+时间戳后6位+随机数6位
        return GenerateStrUtil.getOrderNo();
    }


    public static void main(String[] args) {
        String test = generateOrderNo();
        System.out.println(test);
    }
}
