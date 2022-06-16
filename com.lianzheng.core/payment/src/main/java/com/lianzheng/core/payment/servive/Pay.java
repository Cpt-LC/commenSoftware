package com.lianzheng.core.payment.servive;


import com.lianzheng.core.payment.Result.ResultPay;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public interface Pay {
    //吊起支付
    /**
     *
     * @param req
     * @param orderNo   订单号
     * @param amount    总价
     * @param body  订单标题
     * @param type  支付类型  支付宝：APP/WAP/PAGE  微信：WMEB/NATIVE/APP/JSAPI
     * @param param
     * @return
     * @throws Exception
     */
    ResultPay createdPay(HttpServletRequest req, String orderNo, String amount, String body, String type, Object param) throws Exception;

    //验签
    /**
     *
     * @param requestMap     *
     * @param param 配置参数
     * @return
     */
    ResultPay checkSign(Map<String, String> requestMap, Object param) throws Exception;


    //查询订单状态
    /**
     *
     * @param req
     * @param orderNo 订单号
     * @param param  配置参数
     * @return
     */
    ResultPay getOrderStatus(HttpServletRequest req,String orderNo ,Object param);

//    //退款
//    /**
//     *
//     * @param req
//     * @param orderNo  订单号
//     * @param refund_fee   退款金额
//     * @param total_free    总价
//     * @param param  配置参数
//     * @return
//     */
//    ResultPay refund(HttpServletRequest req,String orderNo,String refund_fee,String total_free,Object param);
}
