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

import org.apache.log4j.Logger;
import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.exception.SandboxNotFoundException;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.fiddle.util.FilterUtil;
import org.zkoss.fiddle.visualmodel.CaseRequest;
import org.zkoss.fiddle.visualmodel.CaseRequest.Type;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.web.servlet.Servlets;

public class FiddleViewFilter implements Filter {

	private static final String FIDDLE_HOST_NAME = "fiddleHostName";

	private static final Logger logger = Logger.getLogger(FiddleViewFilter.class);

	private ServletContext ctx;

	private ICaseDao caseDao;

	private FiddleSandboxManager sandboxManager;

	public void doFilter(ServletRequest request2, ServletResponse response2, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);
		HttpServletResponse response = ((HttpServletResponse) response2);

		String path = FilterUtil.getPath(request2);

		if (path == null || "/".equals(path) || "/service/try".equals(path)) {
			Boolean tryCase = path.equals("/service/try");
			if(tryCase){
				request.setAttribute(FiddleConstant.REQUEST_ATTR_TRY_CASE, tryCase);
			}
			request.setAttribute(FiddleConstant.REQUEST_ATTR_CONTENT_PAGE, FiddleConstant.REQUEST_VALUE_PAGE_TYPE_SOURCE);
			request.setAttribute(FIDDLE_HOST_NAME, FiddleConfig.getHostName());
			Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
			return;
		}

		CaseRequest viewRequest = getAttributeFromURL(path);
		if (viewRequest != null) {
			request.setAttribute(FiddleConstant.REQUEST_ATTR_CONTENT_PAGE, FiddleConstant.REQUEST_VALUE_PAGE_TYPE_SOURCE);
			request.setAttribute(FIDDLE_HOST_NAME, FiddleConfig.getHostName());
			ICase $case = handleCaseInRequest(request, viewRequest);
			if ($case == null) {
				if(viewRequest.getType() == Type.Widget){
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					Servlets.forward(ctx, request, response, "/WEB-INF/_include/widgetnotfound.zul");
				}else{
					((HttpServletResponse) response).sendRedirect("/");
				}
				return;
			}
			if (viewRequest.needInitSandbox()) {
				try {
					initSandbox(response, request, viewRequest);
				} catch (IllegalArgumentException ex) { // sandbox not found
					return;
				}
			}

			if (viewRequest.getType() == Type.Direct) {
				response.sendRedirect(viewRequest.getFiddleDirectURL());
			} else if(viewRequest.getType() == Type.Widget){
				Servlets.forward(ctx, request, response, "/WEB-INF/_include/widget.zul");
			} else {
				Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul");
			}

		} else {
			chain.doFilter(request2, response);
		}

	}

	/* ------------ internal implemenation ------------- */

	protected void initSandbox(HttpServletResponse response, HttpServletRequest request, CaseRequest viewRequest)
			throws IOException {
		try {
			String instHash = ((String) request.getParameter("run"));
			viewRequest.setFiddleSandbox(getSandbox(instHash, viewRequest.getZkversion()));
			request.setAttribute(FiddleConstant.REQUEST_ATTR_RUN_VIEW, viewRequest);
		} catch (SandboxNotFoundException e) {

			/**
			 * 1. if we lookup hash and hash not found , we lookup ZK version
			 * then. 2. if we lookup zkversion not found , we lookup a specific
			 * sandbox by default and will not came in this this one.
			 */
			String url = getURL(viewRequest, e.getType());
			if (url != null) {
				response.sendRedirect(url);
				throw new IllegalArgumentException("Sandbox Not Found");
			}
		}
	}

	protected ICase getCase(String caseToken, String version) {

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

	protected ICase handleCaseInRequest(HttpServletRequest request, CaseRequest viewRequest) throws IOException,
			ServletException {

		// same path between the two

		ICase $case = getCase(viewRequest.getToken(), viewRequest.getTokenVersion());
		if ($case == null) {
			return null;
		}
		// response.sendRedirect("/");

		request.setAttribute(FiddleConstant.REQUEST_ATTR_CASE, $case);
		setPageTitle(request, $case);
		return $case;
	}

	protected String getURL(CaseRequest viewRequest, SandboxNotFoundException.Type type) {

		String tokenLink = viewRequest.getToken() + "/" + viewRequest.getTokenVersion();
		String zkVer = "/v" + viewRequest.getZkversion();

		// means we can't find any sandbox for it , then go back to sample view
		// directly.
		if ((type == SandboxNotFoundException.Type.DEFAULT)) {
			return FiddleConfig.getHostName() + "/sample/" + tokenLink;
		}

		String prefix = viewRequest.getType().getPrefix();

		boolean showVer = (type == SandboxNotFoundException.Type.HASH);
		return FiddleConfig.getHostName() + prefix + tokenLink + (showVer ? zkVer : "");
	}

	protected FiddleSandbox getSandbox(String instHash, String zkver) throws SandboxNotFoundException {
		FiddleSandbox inst;
		SandboxNotFoundException.Type type = SandboxNotFoundException.Type.DEFAULT;
		if (instHash != null) {
			inst = sandboxManager.getFiddleSandbox(instHash);
			type = (SandboxNotFoundException.Type.HASH);
		} else if (zkver != null) {
			inst = sandboxManager.getFiddleSandboxByVersion(zkver);
			type = (SandboxNotFoundException.Type.ZK_VERSION);
		} else {
			inst = sandboxManager.getFiddleSandboxForLastestVersion();
			type = (SandboxNotFoundException.Type.DEFAULT);
		}
		if (inst == null) {
			throw new SandboxNotFoundException(type);
		}
		return inst;
	}

	protected void setPageTitle(HttpServletRequest request, ICase $case) {
		String title = CaseUtil.getPublicTitle($case);
		request.setAttribute(FiddleConstant.REQUEST_ATTR_PAGE_TITLE, " - " + title);
	}


	protected CaseRequest getAttributeFromURL(String path) {
		return CaseRequest.getCaseRequest(path);
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
