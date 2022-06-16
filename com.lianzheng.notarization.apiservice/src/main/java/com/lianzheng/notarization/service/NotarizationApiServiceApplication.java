package com.lianzheng.notarization.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = {
		"com.lianzheng.core.*"
		,"com.lianzheng.notarization.*"
})
@MapperScan({
		"com.lianzheng.notarization.**.dao",
		"com.lianzheng.core.**.dao"
})
@ServletComponentScan(basePackages = "com.lianzheng.core.log")
public class NotarizationApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotarizationApiServiceApplication.class, args);
		System.out.println("==========================NotarizationApiServiceApplication start==================");
	}

}
