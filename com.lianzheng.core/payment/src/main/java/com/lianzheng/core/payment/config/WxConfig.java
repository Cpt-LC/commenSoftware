package com.lianzheng.core.payment.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: demo
 * @Date: 2020/9/23 15:51
 */
@Configuration
public class WxConfig {


    @Bean("WxPayProperties")
    public WxPayProperties wxPayProperties(){
        return new WxPayProperties();
    }
}


