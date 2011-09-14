package org.zkoss.fiddle.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.fiddle.util.UserUtil;
import org.zkoss.web.servlet.Servlets;

public class FiddleAuthFilter extends FiddleViewBaseFilter {


	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		super.doFilter(request, response, chain);
		HttpServletRequest httprequest = (HttpServletRequest) request;

		HttpSession session = httprequest.getSession(true);
		if (UserUtil.isAdmin(session)) {
			if (httprequest.getRequestURL().indexOf("login.zul") != -1) {
				((HttpServletResponse) response)
						.sendRedirect("/admin/index.zul");
			} else {
				chain.doFilter(request, response);
			}
		} else {
			Servlets.forward(ctx, httprequest, response, "/admin/login.zul");
		}
	}

}
