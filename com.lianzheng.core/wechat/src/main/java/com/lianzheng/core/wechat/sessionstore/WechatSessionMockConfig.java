package com.lianzheng.core.wechat.sessionstore;

import com.github.yingzhuo.springboot.env.propertysource.TomlPropertySourceFactory;
import com.lianzheng.core.exceptionhandling.ExceptionFormatter;
import com.lianzheng.core.resource.FileContentUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Log4j2
@Component
@Configuration
@PropertySource(value = "classpath:/config.toml", factory = TomlPropertySourceFactory.class)
public class WechatSessionMockConfig {
    @Value("${wework.session.mock.root}")
    private String root;

    public String getRoot() {
        return root;
    }
    @Value("${wework.session.mock.loadedData}")
    private String loadedData;

    public String getLoadedData() {
        return loadedData;
    }

    public void storeData(String rawData, String prefix, boolean alwaysWrite) {
        if(!alwaysWrite && !this.loadedData.isEmpty()){
            return;
        }
        try {
            Path directory = Paths.get(this.root);
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
            }
            String filePathStr = String.format("%s%s.%s.json", this.root,prefix,
                    new SimpleDateFormat("yyyyMMdd.HHmmss").format(new Date()));
            byte[] content = rawData.getBytes(StandardCharsets.UTF_8);
            Files.write(Paths.get(filePathStr), content);
        }
        catch (Exception ex){
            //保存mock数据，不应影响正常流程
            String errorMessage = "javax.servlet.error.exception:"+ ExceptionFormatter.Format(ex);
            log.error(errorMessage);
        }
    }

    public String getMockData() throws IOException {
        if(this.loadedData.isEmpty()){
            return "";
        }

        String filePathStr = String.format("%s%s", this.root,this.loadedData);
        Path filePath = Paths.get(filePathStr);
        Assert.isTrue(Files.exists(filePath), String.format("File '%s' not exists",filePathStr));
        byte[] bytes = Files.readAllBytes(filePath);
        String content = new String(bytes, StandardCharsets.UTF_8);
        return content;
    }
}
