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
import javax.servlet.http.HttpServletResponse;

import org.zkoss.fiddle.instance.FiddleInstanceManager;
import org.zkoss.fiddle.model.FiddleInstance;
import org.zkoss.fiddle.model.ViewRequest;
import org.zkoss.web.servlet.Servlets;

public class FiddleDispatcherFilter implements Filter {

	public static void main(String[] args) {
		String path = "/3591l7m/2";

		Pattern pattern = Pattern.compile("^/([^/.]{5,}?)/(\\d*)/?.*$");
		Matcher match = pattern.matcher(path);
		if (match.find()) {
			System.out.println(match.group(1));
			System.out.println(match.group(2));
		}

	}

	Pattern code = Pattern.compile("^/code/([^/.]{5,}?)/(\\d+)/?.*$");

	Pattern view = Pattern.compile("^/view/([^/.]{5,}?)/(\\d+)(/v([0-9\\.]+)/?)?.*$");

	public void doFilter(ServletRequest request2, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");

		if (path == null || path.equals("/")) {
			Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
			return;
		}

		if (path.startsWith("/view")) {
			// http://localhost:9999/view/3591l7m/3/v5.0.7?run=TonyQ
			Matcher match = view.matcher(path);
			if (match.find()) {
				/*
				 * if(vr.getInstance() == null){ inst =
				 * manager.getFiddleInstanceByVersion(zkversion); //TODO
				 * redirect this }
				 */

				request.setAttribute("token", match.group(1)); //For SourceCodeEditorComposer
				request.setAttribute("ver", match.group(2));
				
				
				ViewRequest vr = new ViewRequest();
				vr.setToken(match.group(1));
				vr.setVer(match.group(2));
				vr.setZkversion(match.group(4));
				
				FiddleInstanceManager im = FiddleInstanceManager.getInstance();

				String instName = (String) request.getAttribute("run");
				String newurl = null;
				FiddleInstance inst;

				if (instName != null) {
					inst = im.getFiddleInstance(instName);
					if (inst == null) {
						newurl = ("/view/" + vr.getToken() + "/" + vr.getVer() + "/v" + vr.getZkversion());
						((HttpServletResponse) response).sendRedirect(newurl);
						return;
					}
					request.setAttribute("runview", vr);
				} else if (vr.getZkversion() != null) {
					inst = im.getFiddleInstanceByVersion(vr.getZkversion());

					if (inst == null) {
						newurl = ("/view/" + vr.getToken() + "/" + vr.getVer());
						((HttpServletResponse) response).sendRedirect(newurl);
						return;
					}
					request.setAttribute("runview", vr);
				} else {
					inst = im.getFiddleInstanceForLastestVersion();
				}

				vr.setInst(inst);
				Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
				return;
			}
		} else if (path.startsWith("/code")) {
			Matcher match = code.matcher(path);
			if (match.find()) {
				request.setAttribute("token", match.group(1));
				request.setAttribute("ver", match.group(2));
				Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
				return;
			}
		} 
		
		chain.doFilter(request2, response);

	}

	private ServletContext ctx;

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
}
