package com.lianzheng.core.ocr.config;

import com.lianzheng.core.config.TomlParser;
import com.moandjiezana.toml.Toml;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Configuration
/**
 * @Description: ocr 配置装载类
 * @author: 何江雁
 * @date: 2022年02月16日 17:30
 */
@ConfigurationProperties(prefix = "toml")
public class OcrConfig {

    @Value("${outConfigPath}")
    private String outConfigPath;
//    static {
//           try{
//                Yaml yaml = new Yaml();
//                File file = new File("application.yml");
//                InputStream in = new FileInputStream(file);
//                Map<String, Object> map = yaml.loadAs(in, Map.class);
//                in.close();
//                outConfigPath = (String)map.get("outConfigPath");
//            }catch (Exception e){
//                e.printStackTrace();
//
//            }
//    }

    @Bean("ocr")
    public OcrSettings getOcrSettings() throws IOException {
        Toml toml = TomlParser.rawParse(outConfigPath +"config.toml");
        Assert.notNull(toml,"Unable to findout config.toml");
        Toml ocrToml = toml.getTable("ocr");
        Assert.notNull(ocrToml,"Unable to findout the ocr configuration in config.toml");
        OcrSettings  settings = ocrToml.to(OcrSettings.class );
        Assert.isTrue(settings != null && !settings.getType().isEmpty(),"OCR settings is incorrect in config.toml");
        return settings;
    }


}
