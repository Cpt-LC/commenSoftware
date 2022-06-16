package com.lianzheng.core.auth.mgmt.xss;

import javax.servlet.*;
import java.io.IOException;

/**
 * XSS过滤
 *
 * @author gang.shen@kedata.com
 */
public class XssFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
//		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(
//				(HttpServletRequest) request);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}