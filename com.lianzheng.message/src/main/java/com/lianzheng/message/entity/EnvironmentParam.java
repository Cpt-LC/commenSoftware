package com.lianzheng.message.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author lxk
 * @date 2022/6/14
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "environment-param")
public class EnvironmentParam {

    List<AppList> paramList;

    @Data
    public static class AppList {
        private String name;
        private String appId;
    }

}
