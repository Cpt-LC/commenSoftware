package com.lianzheng.core.log;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author kk
 * @date 2021/12/8 11:57
 * @describe
 * @remark
 */

@WebFilter(filterName = "logFilter", urlPatterns = "/*")
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            ServletRequest requestWrapper = null;
            if (servletRequest instanceof HttpServletRequest) {
                // 只有content-type 是json的才读流，不然直接不读
                HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
                if (StringUtils.hasLength(httpServletRequest.getContentType()) && httpServletRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
                    requestWrapper = new LogRequestWrapper((HttpServletRequest) servletRequest);
                }
            }
            if (null == requestWrapper) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                filterChain.doFilter(requestWrapper, servletResponse);
            }
        } catch (ServletException e) {
            System.out.println("[日志过滤器:com.lianzheng.core.log]  ServletException:" + e);
        } catch (IOException e) {
            System.out.println("[日志过滤器:com.lianzheng.core.log]  IOException:" + e);
        } catch (Exception e) {
            System.out.println("[日志过滤器:com.lianzheng.core.log]  Exception:" + e);
        }
    }

    @Override
    public void destroy() {
    }
}