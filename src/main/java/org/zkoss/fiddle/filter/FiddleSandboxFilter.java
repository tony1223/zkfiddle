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

import org.apache.log4j.Logger;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;

public class FiddleSandboxFilter implements Filter {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FiddleSandboxFilter.class);

	private FiddleSandboxManager sandboxManager;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httprequest = ((HttpServletRequest) request);

		int size = -1;
		try {
			String sizePar = httprequest.getParameter("size");
			size = Integer.parseInt(sizePar);
		} catch (Exception ex) {
			logger.warn("size is valid number.", ex);
		}

		
		
		
		if(size !=-1){
			boolean success = true;
			
			for (int i = 0; i < size; ++i) {
				boolean curSuccess = addSandbox(httprequest.getParameter("name"+i), 
						httprequest.getParameter("path"+i),
						httprequest.getParameter("ver"+i)) ;
				success = success && curSuccess;
				
			}
			
			if (success) {
				response.getWriter().println("true");
			} else {
				response.getWriter().println("false");
			}

			response.getWriter().close();
		}else {
			//2011/11/8 Tony: Currently nobody is using this way , plan to remove this in future.
			boolean success = addSandbox(httprequest.getParameter("name"), httprequest.getParameter("path"),
					httprequest.getParameter("ver"));

			if (success) {
				response.getWriter().println("true");
			} else {
				response.getWriter().println("false");
			}

			response.getWriter().close();

		}
	}

	private boolean addSandbox(String name, String path, String ver) {
		FiddleSandbox in = new FiddleSandbox();
		in.setName(name);
		in.setPath(path);
		in.setVersion(ver);
		in.setLastUpdate(new Date());

		try {
			sandboxManager.addFiddleSandbox(in);
			return true;
		} catch (Exception e) {
			logger.error("Exception happened when add sandbox to the queue", e);
			return false;
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}

	public FiddleSandboxManager getSandboxManager() {
		return sandboxManager;
	}

	public void setSandboxManager(FiddleSandboxManager sandboxManager) {
		this.sandboxManager = sandboxManager;
	}
}
