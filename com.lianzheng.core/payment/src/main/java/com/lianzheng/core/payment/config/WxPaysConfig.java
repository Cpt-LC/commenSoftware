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
public class WxPaysConfig {
    @Value("${outConfigPath}")
    private String outConfigPath;

    @Bean("wxPays")
    public List<WxPayProperties> pays() throws IOException {
        Toml toml = TomlParser.rawParse(outConfigPath+ "config.pay.toml");
        List<Toml>  wxpay = toml.getTables("wxpay" );
        List<WxPayProperties> result = new ArrayList<>();
        if(wxpay == null){
            return result;
        }
        wxpay.forEach((t)->{
            WxPayProperties pay = t.to(WxPayProperties.class);
            result.add(pay);
        });
        return result;
    }
}
