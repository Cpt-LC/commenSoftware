package com.lianzheng.core.sign.Utils;


import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.core.sign.config.SignConfig;
import com.lianzheng.core.sign.config.SignsConfig;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log
@Configuration
public class SignUtils {

    @Autowired
    private SignsConfig signsConfig ;


    public HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }

    public  void  checkSign(Map<String,Object> param) throws IOException{
        String timestamp = getRequest().getHeader("timestamp");
        Object appId = param.get("appId");
        Object token = param.get("token");
        Object nonce = param.get("nonce");

        //验证参数有效性
        if(StringUtils.isBlank(timestamp)){
            throw new COREException("时间戳不能为空",5);
        }
        if(appId==null){
            throw new COREException("appId不能为空",1);
        }
        if(token==null){
            throw new COREException("token不能为空",3);
        }
        if(nonce==null){
            throw new COREException("nonce不能为空",6);
        }

        List<SignConfig> signConfigList = signsConfig.signs();
        Map<String, SignConfig> stringListMap = signConfigList.stream().collect(Collectors.toMap(SignConfig::getAppId, Function.identity()));
        SignConfig signConfig = stringListMap.get(appId.toString());
        if(signConfig==null){
            throw new COREException("appId无效",2);
        }

        String sign = nonce.toString()+timestamp+signConfig.getSecretKey();
        String md5 = DigestUtils.md5DigestAsHex(sign.getBytes());
        String encoded = Base64.getEncoder().encodeToString(md5.getBytes());
        log.info(encoded);
        if(!encoded.equals(token)){
            throw new COREException("token验证失败",4);
        }
    }
}
