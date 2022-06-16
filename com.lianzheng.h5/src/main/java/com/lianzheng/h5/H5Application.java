package com.lianzheng.h5;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {
        "com.lianzheng.h5"
        ,"com.lianzheng.core.*"
        ,"com.github.*"
        ,"com.lianzheng.common_service.file_storage_sdk"
})
@MapperScan("com.lianzheng.h5.mapper")
public class H5Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(H5Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}

