package com.lianzheng.notarization.master.configParameter.utils;

import com.lianzheng.notarization.master.configParameter.param.*;
import org.springframework.context.annotation.Configuration;

/**
 * 获取配置参数公告方法
 */
@Configuration
public class ConfigParameterUtil {

    //获取支付参数（单价）
    public static PayArrayParam getPayArray(){
        return new PayArrayParam();
    }

    //获取订单逻辑参数
    public static OrderLogicalParam getOrderLogical(){
        return new OrderLogicalParam();
    }

    //获取短信参数
    public static MessagesParam getMessages(){
        return new MessagesParam();
    }

    //获取国家参数
    public static CountryParam getCountry(){
        return new CountryParam();
    }

    //获取文件相关模板参数
    public static DocumentParam getDocument(){
        return new DocumentParam();
    }

    //获取求真系统参数
    public static TruthParam getTruth(String truthCase){
        return new TruthParam(truthCase);
    }

    //获取文件签名文件相关参数
    public static SignUrlParam getSignUrl(){
        return new SignUrlParam();
    }
}
