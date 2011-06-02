package org.zkoss.fiddle.filter;

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

import org.zkoss.web.servlet.Servlets;

public class FiddleDispatcherFilter implements Filter {

	public static void main(String[] args) {
		String path = "/3591l7m/2"	;
		
		Pattern pattern = Pattern.compile("^/([^/.]{5,}?)/(\\d*)/?.*$");
		Matcher match = pattern.matcher(path);
		if (match.find()) {
			System.out.println(match.group(1));
			System.out.println(match.group(2));
		}
		
	}
	public void doFilter(ServletRequest request2, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");
		
		if(path.equals("/")){
			Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
			return ;
		}
		
		Pattern pattern = Pattern.compile("^/([^/.]{5,}?)/(\\d*)/?.*$");
		Matcher match = pattern.matcher(path);
		if (match.find()) {
			request.setAttribute("token", match.group(1));
			request.setAttribute("ver", match.group(2));
			System.out.println("casePath:"+path);
			Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
			return ;
		} else {
			chain.doFilter(request2, response);
		}

	}

	private ServletContext ctx;

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
}
