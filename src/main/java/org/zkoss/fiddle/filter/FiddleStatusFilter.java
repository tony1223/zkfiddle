package org.zkoss.fiddle.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FiddleStatusFilter implements Filter {


	public void doFilter(ServletRequest request2, ServletResponse response2, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);
		HttpServletResponse response = ((HttpServletResponse) response2);


		String jsonp = request.getParameter("jsonp");
		if(jsonp == null){
			response.getWriter().print("error");
		}else{
			response.getWriter().print(jsonp +"({success:true})");
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}


}
