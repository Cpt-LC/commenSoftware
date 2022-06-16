package com.lianzheng.core.auth.mgmt.config;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Log
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private final Path root = Paths.get("uploads");
    private Path noticesRoot = Paths.get("notices");
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
//            .allowedOrigins("*")
//            .allowedOriginPatterns("*")
//            .allowCredentials(true)
//            .allowedHeaders("*")
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//            .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //上传的图片在D盘下的OTA目录下，访问路径如：http://localhost:8081/OTA/d3cf0281-bb7f-40e0-ab77-406db95ccf2c.jpg
        //其中OTA表示访问的前缀。"file:D:/OTA/"是文件真实的存储路径
//        log.info("file:"+System.getProperty("user.dir")+"/" + this.root+"/");
////        registry.addResourceHandler("/uploads/**").addResourceLocations("file:\\"+System.getProperty("user.dir")+"\\" + this.root+"\\");
////        registry.addResourceHandler("/notices/**").addResourceLocations("file:\\"+System.getProperty("user.dir")+"\\" + this.noticesRoot+"\\");
//        registry.addResourceHandler("/uploads/**").addResourceLocations("file:"+System.getProperty("user.dir")+"/" + this.root+"/");
//        registry.addResourceHandler("/notices/**").addResourceLocations("file:"+System.getProperty("user.dir")+"/" + this.noticesRoot+"/");
    }
}