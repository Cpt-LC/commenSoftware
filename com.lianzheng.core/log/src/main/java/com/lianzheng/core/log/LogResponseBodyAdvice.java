package com.lianzheng.core.log;

import com.alibaba.fastjson.JSON;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author kk
 * @date 2021/12/8 10:33
 * @describe
 * @remark
 */
@ControllerAdvice
public class LogResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
//        HttpServletResponse response = ((ServletServerHttpResponse)serverHttpResponse).getServletResponse();
//
//        String metaType = request.getContentType();
//        if(!StringUtils.hasLength(metaType) || !metaType.contains(MediaType.APPLICATION_JSON_VALUE)){
//            return body;
//        }

        if (body instanceof Map || body instanceof RuntimeException) {
            request.setAttribute("responsebody", JSON.toJSON(body));
        }

        return body;
    }

}
