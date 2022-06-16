package com.lianzheng.core.wechat.sessionstore;

import com.github.yingzhuo.springboot.env.propertysource.TomlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Description: 企业微信会话的配置类
 * @author: 何江雁
 * @date: 2021年10月19日 12:39
 */
@Component
@Configuration
@PropertySource(value = "classpath:/config.toml", factory = TomlPropertySourceFactory.class)
public class WechatSessionConfig {
    @Value("${wework.corpid}")
    private String corpid;

    public String getCorpid(){
        return corpid;
    }

    @Value("${wework.session.secret}")
    private String secret;

    public String getSecret(){
        return secret;
    }

    @Value("${wework.session.proxy}")
    private String proxy;

    public String getProxy(){
        return proxy;
    }

    @Value("${wework.session.proxyPassword}")
    private String proxyPassword;
    public String getProxyPassword(){
        return proxyPassword;
    }

    @Value("${wework.session.timeout}")
    private int timeout;

    public int getTimeout(){
        return limit;
    }

    @Value("${wework.session.limit}")
    private int limit;

    public int getLimit(){
        return limit;
    }

    @Autowired
    private WechatSessionMockConfig mockConfig;

    public WechatSessionMockConfig getMockConfig(){
        return this.mockConfig;
    }
}

