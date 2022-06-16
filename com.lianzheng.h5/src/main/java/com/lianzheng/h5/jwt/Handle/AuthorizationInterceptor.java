package com.lianzheng.h5.jwt.Handle;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lianzheng.h5.entity.SysNotarialOfficeEntity;
import com.lianzheng.h5.jwt.CustomException;
import com.lianzheng.h5.jwt.IgnoreAuth;
import com.lianzheng.h5.service.SysNotarialOfficeService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Log
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {



    public static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";

    public static final String NOTARIAL_OFFICE = "NOTARIAL_OFFICE";
    @Autowired
    private SysNotarialOfficeService sysNotarialOfficeService;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        //支持跨域请求
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        String headerOrigin=httpServletRequest.getHeader("Origin");
//        if(ALLOW_ORIGINS.contains())

        //存入公证处的信息
        String index = httpServletRequest.getHeader("index");
        System.out.println(index);
        if(index == null){
            throw new CustomException("请选择所处公证处");
        }else {
            SysNotarialOfficeEntity sysNotarialOfficeEntity = sysNotarialOfficeService.getById(index);
            if(Objects.isNull(sysNotarialOfficeEntity)||sysNotarialOfficeEntity.getFlag()==1){
                throw new CustomException("不存在该公证处");
            }
            httpServletRequest.setAttribute(NOTARIAL_OFFICE, sysNotarialOfficeEntity);
        }


        String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();

        //检查是否有IgnoreAuth注释，有则跳过认证
        if (method.isAnnotationPresent(IgnoreAuth.class)) {
            IgnoreAuth passToken = method.getAnnotation(IgnoreAuth.class);
            if (passToken.required()) {
                return true;
            }
        }

                // 执行认证
                if (token == null) {
                    log.info(method.getName());
                    System.out.println(method.getName());
                    throw new CustomException("无token，请求无效");
                }
                // 获取 token 中的 user id
                String userId;
                try {
                    userId = JWT.decode(token).getClaims().get("username").asString();
                } catch (JWTDecodeException j) {
                    throw new  CustomException("用户异常，请求无效");
                }
                //设置userId到request里，后续根据userId，获取用户信息
                httpServletRequest.setAttribute(LOGIN_USER_KEY, userId);


                return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}