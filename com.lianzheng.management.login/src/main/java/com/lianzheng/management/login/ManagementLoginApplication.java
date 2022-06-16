package com.lianzheng.management.login;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication(scanBasePackages = {
		"com.lianzheng.management.login"
		,"com.lianzheng.core.*"
		,"com.google.code.*"
		,"org.hibernate.*"
		,"com.github.*"
		,"com.lianzheng.common_service.file_storage_sdk"
})
@MapperScan({
		"com.lianzheng.core.**.dao"
})
@ServletComponentScan(basePackages = "com.lianzheng.core.log")
public class ManagementLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementLoginApplication.class, args);
		System.out.println("==============ManagementLoginApplication service started============");
	}

}
