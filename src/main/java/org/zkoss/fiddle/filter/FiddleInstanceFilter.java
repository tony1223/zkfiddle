package org.zkoss.fiddle.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.fiddle.instance.FiddleInstanceManager;
import org.zkoss.fiddle.model.FiddleInstance;

public class FiddleInstanceFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httprequest = ((HttpServletRequest) request);

		FiddleInstance in = new FiddleInstance();
		in.setName(httprequest.getParameter("name"));
		in.setPath(httprequest.getParameter("path"));
		in.setVersion(httprequest.getParameter("ver"));
		in.setLastUpdate(new Date());
		
		try {
			FiddleInstanceManager im = FiddleInstanceManager.getInstance();
			im.addFiddleInstance(in);
			response.getWriter().println("true");
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println("false");
		}
		response.getWriter().close();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
}
