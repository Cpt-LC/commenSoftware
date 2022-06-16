package com.lianzheng.notarization.master.configParameter.utils;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

@Configuration
public class GetYmlParamUtil {

    private static  String active;

    public static Map<String, Object> getYmlParam(){
        try{
                Yaml yaml = new Yaml();
                File file = new File("application.yml");
                InputStream in = new FileInputStream(file);
                Map<String, Object> map = yaml.loadAs(in, Map.class);
                in.close();
                return map;
        }catch (Exception e){
                 e.printStackTrace();
                 return null;
        }
    }

//    static {
//        try{
//            Yaml yaml = new Yaml();
//            Resource resource = new ClassPathResource("application.yml");
//            InputStream in = resource.getInputStream();
//            Map<String, Object> map = yaml.loadAs(in, Map.class);
//            in.close();
//            Map<String, Object> spring = (Map<String, Object>)map.get("spring");
//            spring = (Map<String, Object>)spring.get("profiles");
//            active= (String)spring.get("active");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

}
