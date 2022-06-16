package com.lianzheng.core.payment.servive.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.lianzheng.core.payment.Result.ResultPay;
import com.lianzheng.core.payment.config.AliPayConfig;
import com.lianzheng.core.payment.servive.Pay;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class alipay implements Pay {

    //吊起支付
    @Override
    public ResultPay createdPay(HttpServletRequest req, String orderNo, String amount, String body, String type, Object param){
        AliPayConfig aliPayConfig = (AliPayConfig)param;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR,Integer.parseInt(aliPayConfig.getTime_expire()));// 24小时制
        Date exTime = cal.getTime();
        String expireTime = format.format(exTime);
        try {
            // 构建支付数据信息
            Map<String, String> data = new HashMap<>();
            data.put("subject", body); //订单标题
            data.put("out_trade_no", orderNo); //商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复      //此处模拟订单号为时间
            data.put("total_amount", amount); //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
            data.put("product_code", "FAST_INSTANT_TRADE_PAY"); //销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
//            data.put("timeout_express", aliPayConfig.getTimeout_express());
            data.put("time_expire", expireTime);



            DefaultAlipayClient alipayRsa2Client = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppid(),
                    aliPayConfig.getAppPrivateKey(),
                    "json",
                    aliPayConfig.getCharset(),
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType()
            );

            String response = "";
            switch (type){
                case "APP":
                    AlipayTradeAppPayRequest requestApp = new AlipayTradeAppPayRequest();// APP支付
                    requestApp.setNotifyUrl(aliPayConfig.getNotifyUrl());
                    requestApp.setReturnUrl(aliPayConfig.getReturnUrl());
                    requestApp.setBizContent(JSON.toJSONString(data));
                    response= alipayRsa2Client.pageExecute(requestApp).getBody();
                    break;
                case "PAGE":
                    AlipayTradePagePayRequest requestPage = new AlipayTradePagePayRequest();  // 网页支付
                    requestPage.setNotifyUrl(aliPayConfig.getNotifyUrl());
                    requestPage.setReturnUrl(aliPayConfig.getReturnUrl());
                    requestPage.setBizContent(JSON.toJSONString(data));
                    response= alipayRsa2Client.pageExecute(requestPage).getBody();
                    break;
                case "WAP":
                    AlipayTradeWapPayRequest requestWap = new AlipayTradeWapPayRequest();  //移动h5
                    requestWap.setNotifyUrl(aliPayConfig.getNotifyUrl());
                    requestWap.setReturnUrl(aliPayConfig.getReturnUrl());
                    requestWap.setBizContent(JSON.toJSONString(data));
                    response= alipayRsa2Client.pageExecute(requestWap).getBody();
                    break;
                default:
                    throw new RuntimeException("未配置该支付");
            }


//            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();// APP支付
//            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 网页支付
//            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();  //移动h5
//            request.setNotifyUrl(aliPayConfig.getNotifyUrl());
//            request.setReturnUrl(aliPayConfig.getReturnUrl());
//            request.setBizContent(JSON.toJSONString(data));
//            String response = alipayRsa2Client.pageExecute(request).getBody();
            log.info(response);
            return new ResultPay(true,"吊起支付宝支付成功",0,response);
        } catch (Exception e) {
            return new ResultPay(false,"吊起支付宝支付失败",-1,"");
        }
    }

    //验签
    @Override
    public ResultPay checkSign(Map<String, String> requestMap, Object param){
        AliPayConfig aliPayConfig = (AliPayConfig)param;
        try {
//            Map<String, String> paramsMap = new HashMap<>();
//            Map<String, String[]> requestMap = request.getParameterMap();
//            requestMap.forEach((key, values) -> {
//                String strs = "";
//                for(String value : values) {
//                    strs = strs + value;
//                }
//                System.out.println(("key值为"+key+"value为："+strs));
//                paramsMap.put(key, strs);
//            });
            Boolean Flag =  AlipaySignature.rsaCheckV1(requestMap, aliPayConfig.getAlipayPublicKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
            return new ResultPay(true,"吊起支付宝支付成功",0,Flag);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return new ResultPay(false,"吊起支付宝支付成功",-1,false);
        }
    }


    @Override
    public ResultPay getOrderStatus(HttpServletRequest req,String orderNo , Object param) {
        AliPayConfig aliPayConfig = (AliPayConfig)param;
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(),aliPayConfig.getAppid(),aliPayConfig.getAppPrivateKey(),"json","GBK",aliPayConfig.getAlipayPublicKey(),"RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderNo);
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            return new ResultPay(true, "调用成功", 0, response);
        }catch(AlipayApiException e){
            e.printStackTrace();
            log.error("支付宝查询订单异常",e);
            return new ResultPay(false, "调用失败", -2, "");
        }
    }


//
//    @Override
//    public ResultPay refund(HttpServletRequest req, String orderNo, String refund_fee, String total_free, Object param) {
//        AliPayConfig aliPayConfig = (AliPayConfig)param;
//        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(),aliPayConfig.getAppid(),aliPayConfig.getAppPrivateKey(),"json","GBK",aliPayConfig.getAlipayPublicKey(),"RSA2");
//        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
//        JSONObject bizContent = new JSONObject();
//        bizContent.put("trade_no",orderNo);
//        bizContent.put("refund_amount",total_free);
//        bizContent.put("out_request_no", "HZ01RF001");
//
////// 返回参数选项，按需传入
////JSONArray queryOptions = new JSONArray();
////queryOptions.add("refund_detail_item_list");
////bizContent.put("query_options", queryOptions);
//        request.setBizContent(bizContent.toString());
//        try {
//            AlipayTradeRefundResponse response = alipayClient.execute(request);
//            return new ResultPay(true, "调用退款成功", 0, response);
//        }catch (AlipayApiException e){
//            e.printStackTrace();
//            log.error("支付宝退款异常",e);
//            return new ResultPay(false, "调用退款失败", -2, "");
//        }
//
//    }

}
