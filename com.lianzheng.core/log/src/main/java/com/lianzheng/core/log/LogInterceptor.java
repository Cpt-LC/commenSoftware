package com.lianzheng.core.log;

import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.log.curd.entity.LogEntity;
import com.lianzheng.core.log.curd.entity.SysSpecificLogEntity;
import com.lianzheng.core.log.curd.service.LogService;
import com.lianzheng.core.log.curd.service.SysSpecificLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author kk
 * @date 2021年12月7日
 * @describe 拦截器，只做日志记录，遇到错误或者异常应该直接吃掉。
 * @remark 除非强制要求带 requestId，可抛出异常
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Autowired
    private LogService logService;
    @Autowired
    private SysSpecificLogService sysSpecificLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 需要catch异常，这部分只是日志记录，即使出错也不能影响后续运行
        try {
            LogEntity logEntity = new LogEntity();
            logEntity.setUrl(request.getRequestURI());
            Enumeration<String> headerNames = request.getHeaderNames();
            Map<String, Object> headers = new HashMap<>();
            String requestId = "";
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String headerStr = request.getHeader(name);
                headers.put(name, headerStr);
                if ("requestid".equals(name) || "requestId".equals(name)) {
                    requestId = headerStr;
                }
            }
            requestId = StringUtils.hasLength(requestId) ? requestId : UUID.randomUUID().toString().replaceAll("-", "");
            logEntity.setRequestId(requestId);
            request.setAttribute("requestId", requestId);
            response.addHeader("requestId", requestId);

            if (StringUtils.hasLength(request.getHeader("referer"))) {
                logEntity.setReferer(request.getHeader("referer"));
            }
            if (!headers.isEmpty()) {
                logEntity.setRequestHeaders(new JSONObject(headers).toJSONString());
            }
            logEntity.setDuration(-1);
            logEntity.setStatusCode(-1);

            Map parameterMap = request.getParameterMap();
            if (!parameterMap.isEmpty()) {
                logEntity.setRequestParams(new JSONObject(parameterMap).toJSONString());
            }
            logEntity.setClientIp(IPUtils.getIpAddr(request));
            // request_body 直接读流的方式会导致后面收不到请求参数,需要使用过滤器的方式重写一下
            if (StringUtils.hasLength(request.getContentType()) && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
                LogRequestWrapper logRequestWrapper = new LogRequestWrapper(request);
                String body = logRequestWrapper.getBody();
                logEntity.setRequestBody(body);
            }
            logEntity.setMethod(request.getMethod());
            request.setAttribute("start-log-time", System.currentTimeMillis());
            logEntity.setMessage("<--  " + logEntity.getMethod() + "  " + logEntity.getUrl());
            logService.save(logEntity);

            if(handler instanceof HandlerMethod){
                HandlerMethod handlerMethod=(HandlerMethod)handler;
                Method method=handlerMethod.getMethod();
                if (method.isAnnotationPresent(SpecificLog.class)) {
                    SpecificLog specificLog = method.getAnnotation(SpecificLog.class);
                    SysSpecificLogEntity sysSpecificLogEntity =  new SysSpecificLogEntity();
                    BeanUtils.copyProperties(logEntity,sysSpecificLogEntity);
                    sysSpecificLogEntity.setMark(specificLog.message());
                    sysSpecificLogService.save(sysSpecificLogEntity);
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("LogInterceptor---preHandle");
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            LogEntity logEntity = new LogEntity();
            logEntity.setUrl(request.getRequestURI());
//            Enumeration<String> headerNames = request.getHeaderNames();
//            Map<String, Object> headers = new HashMap<>();
//            while (headerNames.hasMoreElements()) {
//                String name = headerNames.nextElement();
//                String headerStr = request.getHeader(name);
//                headers.put(name, headerStr);
//                if ("requestId".equals(name)) {
//                    logEntity.setRequestId(headerStr);
//                }
//            }
            String requestId = request.getAttribute("requestId") == null ? "" : (String) request.getAttribute("requestId");
            response.setHeader("requestId", requestId);
            logEntity.setRequestId(requestId);
            logEntity.setClientIp(IPUtils.getIpAddr(request));
            logEntity.setMethod(request.getMethod());
            Collection<String> responseNames = response.getHeaderNames();
            Map<String, Object> responseHeadersMap = new HashMap<>();
            for (String responseName : responseNames) {
                String headerStr = response.getHeader(responseName);
                responseHeadersMap.put(responseName, headerStr);
            }
            if (!responseHeadersMap.isEmpty()) {
                logEntity.setResponseHeaders(new JSONObject(responseHeadersMap).toJSONString());
            }
            logEntity.setStatusCode(response.getStatus());

            Long duration = request.getAttribute("start-log-time") == null ? -1 : System.currentTimeMillis() - (Long) request.getAttribute("start-log-time");
            logEntity.setDuration(duration.intValue());


            if (request.getAttribute("responsebody") != null) {
                logEntity.setResponseBody(request.getAttribute("responsebody").toString());
            }
            logEntity.setMessage("-->  " + logEntity.getMethod() + "  " + logEntity.getUrl() + " " + logEntity.getStatusCode() + "  " + logEntity.getDuration() + "ms");
            logService.save(logEntity);
        } catch (Exception e) {
            System.out.println("LogInterceptor---afterCompletion出错了");
            e.printStackTrace();
        }

    }

}
