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

import org.zkoss.web.servlet.Servlets;

public class FiddleAuthFilter implements Filter {

	private ServletContext ctx;
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httprequest = (HttpServletRequest) request;
		
		HttpSession s = httprequest.getSession(true);
		
		if( s.getAttribute("login") == null || (Boolean) s.getAttribute("login") != true){
			Servlets.forward(ctx, httprequest, response, "/admin/login.zul");
		}else{
			if(httprequest.getRequestURL().indexOf("login.zul")!=-1){
				((HttpServletResponse)response).sendRedirect("/admin/index.zul");
			}else{
				chain.doFilter(request, response);
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void destroy() {

	}

}
