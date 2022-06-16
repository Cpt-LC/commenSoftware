//package com.lianzheng.core.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.MapPropertySource;
//import org.springframework.core.env.MutablePropertySources;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Description: Mysql的包装类
// * @author: 何江雁
// * @date: 2021年10月07日 13:53
// */
//@Component
//@Configuration
//@PropertySource(name="db.*", value = "classpath:/config.toml", factory = com.github.yingzhuo.springboot.env.propertysource.TomlPropertySourceFactory.class)
//public class MySqlExtension {
//
////    @Autowired
////    private DbInstanceConfig appDB;
//
//    @Autowired
//    private ConfigurableEnvironment env;
//
//
//    private HashMap connectonStrings = new HashMap();
//    public void init(){
//        MutablePropertySources propertySources = env.getPropertySources();
//        MapPropertySource propertySource = (MapPropertySource)propertySources.get("db.*");
//        Map<String, Object> sources = propertySource.getSource();
//        for (String key : sources.keySet()
//        ) {
//            System.out.println(key);
//            String dbConnectionString = (String )sources.get(key);
//            System.out.println(dbConnectionString);
//            connectonStrings.put(key, dbConnectionString);
//        }
//    }
//}
