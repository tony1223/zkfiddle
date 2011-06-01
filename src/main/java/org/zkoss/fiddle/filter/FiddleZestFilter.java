package org.zkoss.fiddle.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.zest.sys.ZestFilter;

public class FiddleZestFilter extends ZestFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httprequest = ((HttpServletRequest)request);
		String path = httprequest.getRequestURI();
  		if(path.startsWith(httprequest.getContextPath()+"/zkau") || path.startsWith(httprequest.getContextPath()+"/web")) {
  			chain.doFilter(httprequest, response);
  		}else{
			super.doFilter(request, response, chain);
  		}
		
	}
}
