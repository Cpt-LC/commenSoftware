package com.lianzheng.core.payment.payFactory;


import com.lianzheng.core.payment.servive.Impl.alipay;
import com.lianzheng.core.payment.servive.Impl.wxpay;
import com.lianzheng.core.payment.servive.Pay;

public class PayFactory {

    //获取支付对象
    public Pay getPayType(String type){
        if(type == null){
            return null;
        }
        if(type.equalsIgnoreCase("WX")){
            return new wxpay();
        } else if(type.equalsIgnoreCase("ALI")){
            return new alipay();
        }
        return null;
    }

}
