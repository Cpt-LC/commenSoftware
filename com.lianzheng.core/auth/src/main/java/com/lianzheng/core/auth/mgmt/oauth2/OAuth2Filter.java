package com.lianzheng.core.auth.mgmt.oauth2;

import com.google.gson.Gson;
import com.lianzheng.core.auth.mgmt.entity.SysUserTokenEntity;
import com.lianzheng.core.auth.mgmt.service.ShiroService;
import com.lianzheng.core.auth.mgmt.utils.HttpContextUtils;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.core.server.ResponseEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * oauth2过滤器
 *
 * @author gang.shen@kedata.com
 */
public class OAuth2Filter extends AuthenticatingFilter {
    @Autowired
    private ShiroService shiroService;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token
        String token = getRequestToken((HttpServletRequest) request);

        if(StringUtils.isBlank(token)){
            return null;
        }

        return new OAuth2Token(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if(((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())){
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        //获取请求token，如果token不存在，直接返回401
        String token = getRequestToken((HttpServletRequest) request);
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest  httpRequest=(HttpServletRequest) request;

        //这里添加了跨域配置
//        httpResponse.setHeader("Access-Control-Allow-Headers", "*");
//        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
//        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//        httpResponse.setHeader("Access-Control-Max-Age", "3600");
//        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        if(StringUtils.isBlank(token)){

            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());

            String json = new Gson().toJson(ResponseBase.error(ResponseEnum.TOKEN_FAIL.getCode(), "invalid token"));

            httpResponse.getWriter().print(json);

            return false;
        }

        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            //根据accessToken，查询用户信息
            ResponseBase r = ResponseBase.error(ResponseEnum.TOKEN_FAIL.getCode(), throwable.getMessage());
            String json = new Gson().toJson(r);
            httpResponse.getWriter().print(json);
        } catch (IOException e1) {

        }

        return false;
    }

    /**
     * 获取请求的token
     */
    private String getRequestToken(HttpServletRequest httpRequest){
        //从header中获取token
        String token = httpRequest.getHeader("token");

        //如果header中不存在token，则从参数中获取token
        if(StringUtils.isBlank(token)){
            token = httpRequest.getParameter("token");
        }

        return token;
    }


}
