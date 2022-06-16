
package com.lianzheng.core.payment.config;

import com.lianzheng.core.config.TomlParser;
import com.moandjiezana.toml.Toml;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Configuration
public class AliPaysConfig {
    
    @Value("${outConfigPath}")
    private String outConfigPath;

    @Bean("aliPays")
    public List<AliPayConfig> pays() throws IOException {
        Toml toml = TomlParser.rawParse(outConfigPath + "config.pay.toml");
        List<Toml>  alipay = toml.getTables("alipay" );
        List<AliPayConfig> result = new ArrayList<>();
        if(alipay == null){
            return result;
        }
        alipay.forEach((t)->{
            AliPayConfig pay = t.to(AliPayConfig.class);
            result.add(pay);
        });
        return result;
    }
}
