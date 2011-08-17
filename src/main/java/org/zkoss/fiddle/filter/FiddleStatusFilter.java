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

import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.util.FilterUtil;
import org.zkoss.fiddle.util.SpringUtilz;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class FiddleStatusFilter implements Filter {

	private ServletContext ctx;

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request2, ServletResponse response2, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);
		HttpServletResponse response = ((HttpServletResponse) response2);

		String path = FilterUtil.getPath(request2);

		if (path.equals("/status/versions")) {
			JSONArray obj = new JSONArray();

			FiddleSandboxManager fiddleSandboxMananger = (FiddleSandboxManager)
				SpringUtilz.getBean(ctx, "sandboxManager");
			obj.addAll(fiddleSandboxMananger.getAvailableVersions());
			response.getWriter().print(FilterUtil.buildJSONP(request, obj));
		} else {
			JSONObject obj = new JSONObject();
			obj.put("success", true);
			response.getWriter().print(FilterUtil.buildJSONP(request, obj));
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void destroy() {
	}

}
