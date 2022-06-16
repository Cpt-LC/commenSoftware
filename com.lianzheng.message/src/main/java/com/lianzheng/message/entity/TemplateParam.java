package com.lianzheng.message.entity;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "tencent-param")
public class TemplateParam {

    // 这里的名字要和yml中的对应字段名称一致
    private  List<TencentParam> paramList;
}
