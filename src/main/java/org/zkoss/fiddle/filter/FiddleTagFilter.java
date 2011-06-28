package org.zkoss.fiddle.filter;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.servlet.Servlets;

public class FiddleTagFilter implements Filter {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FiddleTagFilter.class);

	private Pattern tag = Pattern.compile("^/tag/(.*?)(;jsessionid=.*)?$");

	private ServletContext ctx;
	
	public void doFilter(ServletRequest request2, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");

		Matcher m = tag.matcher(path);
		if(m.find()){
			String mtag = m.group(1);
			if(mtag == null || "".equals(mtag.trim())){
				((HttpServletResponse)response).sendRedirect("/");
				return ;
			}
			request.setAttribute("tag",mtag);
			if (logger.isInfoEnabled()) {
				logger.info("doFilter(ServletRequest, ServletResponse, FilterChain) - Tag Name=" + m.group(1));
			}

			Servlets.forward(ctx, request, response, "/WEB-INF/_include/tag.zul" );
			
		}else
			chain.doFilter(request2, response);

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void destroy() {
	}

	
}
