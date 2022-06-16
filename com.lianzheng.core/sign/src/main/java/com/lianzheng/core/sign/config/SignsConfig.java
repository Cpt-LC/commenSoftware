package com.lianzheng.core.sign.config;


import com.lianzheng.core.config.TomlParser;
import com.moandjiezana.toml.Toml;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Configuration
public class SignsConfig {

    @Value("${outConfigPath}")
    private String outConfigPath;

    @Bean("sign")
    public List<SignConfig> signs() throws IOException {
        Toml toml = TomlParser.rawParse(outConfigPath+"config.sign.toml");
        List<Toml>  sign = toml.getTables("sign" );
        List<SignConfig> result = new ArrayList<>();
        if(sign == null){
            return result;
        }
        sign.forEach((t)->{
            SignConfig signConfig = t.to(SignConfig.class);
            result.add(signConfig);
        });
        return result;
    }
}
