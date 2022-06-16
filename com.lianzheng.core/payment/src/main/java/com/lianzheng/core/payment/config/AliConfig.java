package com.lianzheng.core.payment.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliConfig {
    @Bean
    public  AliPayConfig aliPayConfig(){
        return new AliPayConfig();
    }
}
