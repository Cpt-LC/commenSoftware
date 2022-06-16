package com.lianzheng.core.payment.servive.Impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.jfinal.kit.StrKit;
import com.lianzheng.core.payment.Result.ResultPay;
import com.lianzheng.core.payment.config.WxPayProperties;
import com.lianzheng.core.payment.kit.IpKit;
import com.lianzheng.core.payment.servive.Pay;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class wxpay implements Pay {

    @Override
    //吊起支付
   public ResultPay createdPay(HttpServletRequest req, String orderNo, String amount, String body, String type, Object param) throws Exception{
       WxPayProperties wxPayProperties =(WxPayProperties)param;
       //订单超时时间
       SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
       Calendar cal = Calendar.getInstance();
       cal.setTime(new Date());
       cal.add(Calendar.HOUR,Integer.parseInt(wxPayProperties.getTimeExpire()));// 24小时制
       Date exTime = cal.getTime();
       String expireTime = format.format(exTime);

       Map<String, String> returnMap = new HashMap<>();
       Map<String, String> responseMap = new HashMap<>();
       String ip = IpKit.getRealIp(req);
       if (StrKit.isBlank(ip)) {
           ip = "127.0.0.1";
       }

       WXPay wxpay = new WXPay(wxPayProperties);
       Map<String, String> data = new HashMap<String, String>();
       data.put("body", body);
       data.put("out_trade_no", orderNo);
       data.put("total_fee", amount);
       data.put("nonce_str", WXPayUtil.generateNonceStr());
       data.put("spbill_create_ip", ip);
       data.put("notify_url", wxPayProperties.getNotifyUrl());
       data.put("time_expire", expireTime);

       switch (type){
           case "APP":
               data.put("trade_type", "APP");
               break;
           case "NATIVE":
               data.put("trade_type", "NATIVE");
               break;
           case "MWEB":
               data.put("trade_type", "MWEB");
               break;
           case "JSAPI":
               data.put("trade_type", "JSAPI");
               data.put("openid",wxPayProperties.getOpenId());
               break;
           default:
               throw new RuntimeException("未配置该支付");
       }

           Map<String, String> resultMap = wxpay.unifiedOrder(data);

           //获取返回码
           String returnCode = resultMap.get("return_code");
           String returnMsg = resultMap.get("return_msg");
           //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
           if ("SUCCESS".equals(returnCode)) {
               String resultCode = resultMap.get("result_code");
               if ("SUCCESS".equals(resultCode)) {
                   responseMap = resultMap;
               }
           }
           if (responseMap == null || responseMap.isEmpty()) {
               String errCodeDes = resultMap.get("err_code_des");
               return new ResultPay(false,"发送微信支付请求失败:"+returnMsg,-1,resultMap);
           }

           // 3、签名生成算法
           Long time = System.currentTimeMillis() / 1000;
           String timestamp = time.toString();


           returnMap.put("appId", wxPayProperties.getAppID());
           returnMap.put("timeStamp", timestamp);//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
           returnMap.put("nonceStr", responseMap.get("nonce_str"));
           returnMap.put("package", "prepay_id=" + responseMap.get("prepay_id"));
           returnMap.put("signType", "MD5");

           //生成支付二维码的地址
            if(responseMap.get("code_url")!=null){
                returnMap.put("code_url",responseMap.get("code_url"));
            }
           returnMap.put("sign", WXPayUtil.generateSignature(returnMap, wxPayProperties.getKey()));//微信支付签名
           returnMap.put("return_code",resultMap.get("return_code"));
           returnMap.put("result_code",resultMap.get("result_code"));
           if(responseMap.get("mweb_url")!=null){
                returnMap.put("mweb_url",responseMap.get("mweb_url"));
           }

           return new ResultPay(true,"发送微信支付请求成功",0,returnMap);

   }

    @Override
    //验签
    public ResultPay checkSign(Map<String, String> requestMap, Object param) throws Exception {
        WxPayProperties wxPayProperties =(WxPayProperties)param;
        InputStream is = null;
        String xmlBack = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[报文为空]]></return_msg></xml> ";


            WXPay wxpayApp = new WXPay(wxPayProperties);
            if (wxpayApp.isPayResultNotifySignatureValid(requestMap)) {
                String returnCode = requestMap.get("return_code");  //状态
                String out_trade_no = requestMap.get("out_trade_no");
                if (returnCode.equals("SUCCESS")) {
                    log.info("微信手机支付回调成功订单号:{"+out_trade_no+"}");
                    xmlBack = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    requestMap.put("xmlBack",xmlBack);
                    return new ResultPay(false,"微信验签成功",0,requestMap);
                }
        }
        //返回失败
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("return_code","FAIL");
        resultMap.put("return_msg","微信手机支付回调通知失败");
        resultMap.put("xmlBack",xmlBack);
        return new ResultPay(false,"微信验签失败",0,resultMap);
    }


    @Override
    public ResultPay getOrderStatus(HttpServletRequest req, String orderNo, Object param){
        WxPayProperties wxPayProperties =(WxPayProperties)param;
        WXPay wxpay = new WXPay(wxPayProperties);
        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", orderNo);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        try{
            Map<String, String> resultMap =wxpay.orderQuery(data);
            return new ResultPay(true,"订单查询成功",0,resultMap);
        }catch (Exception e){
            e.printStackTrace();
            log.error("微信查询订单异常",e);
            return new ResultPay(false,"订单查询失败",-2,"");
        }
    }


//    @Override
//    public ResultPay refund(HttpServletRequest req, String orderNo, String refund_fee, String total_free, Object param) {
//        WxPayProperties wxPayProperties =(WxPayProperties)param;
//        Map<String, String> responseMap = new HashMap<>();
//        Map<String, String> requestMap = new HashMap<>();
//        WXPay wxpay = new WXPay(wxPayProperties);
//        requestMap.put("out_trade_no", orderNo);
//        //TODO 做退款时这个需改out_refund_no
//        requestMap.put("out_refund_no", "");
//        requestMap.put("total_fee", total_free);
//        requestMap.put("refund_fee", refund_fee);//所需退款金额
//        try {
//            responseMap = wxpay.refund(requestMap);
//            return new ResultPay(true,"订单退款成功",0,responseMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("微信查询订单异常",e);
//            return new ResultPay(false,"订单退款失败",-2,"");
//        }
//    }
}
