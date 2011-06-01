package org.zkoss.fiddle.filter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.zkoss.fiddle.dao.CaseDaoImpl;
import org.zkoss.fiddle.dao.ResourceDaoImpl;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zkplus.hibernate.HibernateUtil;

public class FiddleDataFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httprequest = ((HttpServletRequest) request);

		String uri = httprequest.getRequestURI();
		String context = httprequest.getContextPath();

		String path = uri.replaceFirst(context, "");
		Pattern pattern = Pattern.compile("/data/([^/]+)(/(.*))?");

		Matcher match = pattern.matcher(path);

		String token = null;
		Integer version = null;

		try {
			if (match.find()) {
				token = match.group(1);
				version = match.group(3) == null ? 1 : Integer.parseInt(match.group(3));
			}
		} catch (Exception e) {
			return;
		}

		if (token == null)
			return;

		// we have to open a one because we are in filter :(
		Session s = HibernateUtil.getSessionFactory().openSession();

		Case c = null; // new Case();
		String caseToken = (String) token;
		if (caseToken != null) {
			ICaseDao caseDao = new CaseDaoImpl(s);

			try {
				c = caseDao.findCaseByToken(caseToken, version);
			} catch (IllegalArgumentException e) {
			}
		}
		if (c == null) {
			return;
		}

		IResourceDao dao = new ResourceDaoImpl(s);
		List<Resource> dbResources = dao.listByCase(c.getId());

		JSONArray json = new JSONArray();

		for (Resource ir : dbResources) {
			JSONObject obj = new JSONObject();
			obj.put("type", ir.getType());
			obj.put("name", ir.getName());
			obj.put("content", ir.getContent());

			json.push(obj);
		}

		JSONObject jsonres = new JSONObject();
		jsonres.put("token", caseToken);
		jsonres.put("ver", version);
		jsonres.put("resources", json);

		response.getWriter().println(jsonres);
		s.close();
		response.getWriter().close();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
}
