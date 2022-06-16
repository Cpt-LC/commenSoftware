package com.lianzheng.core.wechat.externalcontact;

import com.github.yingzhuo.springboot.env.propertysource.TomlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Description: 企业微信的外部用户信息实体类
 * @author: 何江雁
 * @date: 2021年10月26日 17:40
 */
@Component
@Configuration
@PropertySource(value = "classpath:/config.toml", factory = TomlPropertySourceFactory.class)
public class ExternalContractConfig {
    @Value("${wework.corpid}")
    private String corpid;

    public String getCorpid(){
        return corpid;
    }

    @Value("${wework.externalcontract.secret}")
    private String secret;

    public String getSecret(){
        return secret;
    }
}
