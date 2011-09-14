package org.zkoss.fiddle.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.fiddle.util.FilterUtil;

public class FiddleViewBaseFilter implements Filter {
	protected ServletContext ctx;

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void doFilter(ServletRequest request2, ServletResponse response2,
			FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request2).getSession(true);
		FilterUtil.checkAndLoginIfCookieExist(((HttpServletRequest) request2),
				((HttpServletResponse) response2), session, ctx);
	}

	public void destroy() {

	}

}
