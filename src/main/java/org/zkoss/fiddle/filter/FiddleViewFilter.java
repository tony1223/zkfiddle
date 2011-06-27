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

import org.apache.log4j.Logger;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.fiddle.visualmodel.ViewRequest;
import org.zkoss.web.servlet.Servlets;

public class FiddleViewFilter implements Filter {

	private static final Logger logger = Logger.getLogger(FiddleViewFilter.class);

	private Pattern code = Pattern.compile("^/sample/([^/.]{5,}?)/(\\d+)/?.*$");

	private Pattern view = Pattern.compile("^/([^/.]{5,}?)/(\\d+)(/v([0-9\\.]+)/?)?.*$");

	private ServletContext ctx;
	
	private ICaseDao caseDao; 
	
	private FiddleSandboxManager sandboxManager; 

//	public static void main(String[] args) {
//		String path = "/3591l7m/2";
//
//		Pattern pattern = Pattern.compile("^/([^/.]{5,}?)/(\\d*)/?.*$");
//		Matcher match = pattern.matcher(path);
//		if (match.find()) {
//			System.out.println(match.group(1));
//			System.out.println(match.group(2));
//		}
//
//	}

	private String getHostpath(HttpServletRequest request) {
		StringBuffer hostName = new StringBuffer("zkfiddle.org");
		//FIXME 
		/*
		StringBuffer hostName = new StringBuffer(request.getServerName());
		if (request.getLocalPort() != 80) {
			hostName.append(":" + request.getLocalPort());
		}*/
		
		if ("".equals(request.getContextPath())) {
			hostName.append("/" + request.getContextPath());
		} 
		return "http://" + hostName.toString();

	}

	private ICase getCase(String caseToken, String version) {

		ICase $case = null; // new Case();

		if (caseToken != null) {

			try {
				$case = caseDao.findCaseByToken(caseToken, version == null ? 1 : Integer.parseInt(version));
			} catch (IllegalArgumentException e) { // means caseId is not valid

				if (logger.isEnabledFor(org.apache.log4j.Level.WARN)) {
					logger.warn("getCase(String, String) - caseId is not valid ", e);
				}
			} finally {
			}

		}
		return $case;
	}

	private boolean handleView(HttpServletRequest request, HttpServletResponse response, String path)
			throws IOException, ServletException {
		request.setAttribute("hostName", getHostpath(request));
		boolean directly = path.startsWith("/direct");
		String newpath = directly ? path.substring(7) : path.substring(5);
		// inst.getPath() + evt.getToken() + "/" + evt.getVersion()
		// http://localhost:9999/view/3591l7m/3/v5.0.7?run=TonyQ
		Matcher match = view.matcher(newpath);
		if (match.find()) {

			String version = match.group(2);
			String token = match.group(1);

			ICase $case = getCase(token, version);
			if ($case == null) {
				response.sendRedirect("/");
				return true;
			}

			request.setAttribute("__case", $case);
			
			String host = getHostpath(request);
			request.setAttribute("hostName", host);
			request.setAttribute("caseUrl", host + "/sample/"+ $case.getCaseUrl());
			
			setPageTitle(request,$case);
			
			ViewRequest vr = new ViewRequest();
			vr.setToken(match.group(1));
			vr.setTokenVersion(match.group(2));
			vr.setZkversion(match.group(4));

			String instHash = (String) request.getAttribute("run");
			String newurl = null;
			FiddleSandbox inst;

			if (instHash != null) {
				inst = sandboxManager.getFiddleInstance(instHash);
				if (inst == null) {
					newurl = ("/view/" + vr.getToken() + "/" + vr.getTokenVersion() + "/v" + vr.getZkversion());
					response.sendRedirect(newurl);
					return true;
				}
				request.setAttribute("runview", vr);
			} else if (vr.getZkversion() != null) {
				inst = sandboxManager.getFiddleInstanceByVersion(vr.getZkversion());

				if (inst == null) {
					newurl = ("/view/" + vr.getToken() + "/" + vr.getTokenVersion());
					response.sendRedirect(newurl);
					return true;
				}
				request.setAttribute("runview", vr);
			} else {
				inst = sandboxManager.getFiddleInstanceForLastestVersion();
			}

			vr.setFiddleInstance(inst);

			if (directly) {
				response.sendRedirect(inst.getPath() + vr.getToken() + "/" + vr.getTokenVersion());
			} else
				Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
			return true;

		} else {
			response.sendRedirect("/");
			return true;
		}
	}
	
	public void setPageTitle(HttpServletRequest request, ICase $case) {
		boolean emptytitle = ($case.getTitle() == null || "".equals(($case.getTitle().trim())));
		String title = emptytitle ? "Unnamed" : $case.getTitle();
		request.setAttribute("_pgtitle", " - " + title);
	}

	public void doFilter(ServletRequest request2, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");
		
		//TODO review this and move it to global config , we can't count on a filter to handle this. 
		request.setAttribute("hostName", getHostpath(request));
		
		if (path == null || path.equals("/")) {
			Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
			return;
		}

		if (path.startsWith("/view/") || path.startsWith("/direct/")) {
			if (handleView(request, (HttpServletResponse) response, path)) {
				return;
			}
		} else if (path.startsWith("/sample/")) {
			Matcher match = code.matcher(path);
			if (match.find()) {

				String version = match.group(2);
				String token = match.group(1);

				ICase $case = getCase(token, version);
				if ($case == null) {
					((HttpServletResponse) response).sendRedirect("/");
					return;
				}
				String host = getHostpath(request);
				setPageTitle(request, $case);		
				request.setAttribute("hostName", host);
				request.setAttribute("caseUrl", host  + "/sample/" + $case.getCaseUrl());
				request.setAttribute("__case", $case);
				Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
				return;
			} else {
				((HttpServletResponse) response).sendRedirect("/");
				return;
			}
		}

		chain.doFilter(request2, response);

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void destroy() {
	}

	
	public void setCaseDao(ICaseDao caseDao) {
		this.caseDao = caseDao;
	}
	
	public void setSandboxManager(FiddleSandboxManager sandboxManager) {
		this.sandboxManager = sandboxManager;
	}
}
