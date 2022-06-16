package com.lianzheng.management.service;

import com.lianzheng.management.service.modules.notarization.quartz.service.JobService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

//强迫加载
import com.github.yingzhuo.springboot.env.propertysource.TomlPropertySourceFactory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.lianzheng.management.service"
        ,"com.lianzheng.core.*"
        ,"com.google.code.*"
        ,"org.hibernate.*"
        ,"com.github.*"
        ,"com.lianzheng.notarization.*"
        ,"com.lianzheng.common_service.file_storage_sdk"
        })
@MapperScan({
        "com.lianzheng.management.service.**.dao",
        "com.lianzheng.core.**.dao",
        "com.lianzheng.notarization.**.dao",
        })
@ServletComponentScan(basePackages = "com.lianzheng.core.log")
public class ManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementServiceApplication.class, args);
        System.out.println("==============Management service started============");
    }

}
