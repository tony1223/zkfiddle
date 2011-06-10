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

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.zkoss.fiddle.dao.CaseDaoImpl;
import org.zkoss.fiddle.dao.ResourceDaoImpl;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.manager.VirtualCaseManager;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.VirtualCase;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zkplus.hibernate.HibernateUtil;

public class FiddleDataFilter implements Filter {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FiddleDataFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - start");
		}

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
			logger.error("doFilter(ServletRequest, ServletResponse, FilterChain)", e);

			if (logger.isDebugEnabled()) {
				logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - end");
			}
			return;
		}

		if (token == null){
			return;
		}

		try {
			
			boolean result = false;
			if (version != 0){
				result = renderCase(token, version, response);
			}else{
				result = renderVisualCase(token, version, response);
			}
			
			if(!result){ //means failed when writing resource/case
				response.getWriter().println("false");
				response.getWriter().close();	
			}
			
		} catch (Exception ex) {
			logger.error("doFilter(ServletRequest, ServletResponse, FilterChain)", ex);

			response.getWriter().println("false");
			response.getWriter().close();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - end");
		}
	}

	private boolean renderVisualCase(String token, Integer version, ServletResponse response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("renderVisualCase(String, Integer, ServletResponse) - start");
		}

		VirtualCase vc = VirtualCaseManager.getInstance().find(token);
		if (vc == null) {
			return false; //"virtual case not found"
		}
		JSONArray json = renderResources(vc.getResources());

		JSONObject jsonres = new JSONObject();
		jsonres.put("token", token);
		jsonres.put("resources", json);

		response.getWriter().println(jsonres);

		if (logger.isDebugEnabled()) {
			logger.debug("renderVisualCase(String, Integer, ServletResponse) - end");
		}
		return true;
	}

	private boolean renderCase(String token, Integer version, ServletResponse response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("renderCase(String, Integer, ServletResponse) - start");
		}

		// we have to open a one because we are in filter :(
		Session s = HibernateUtil.getSessionFactory().openSession();

		Case c = null; // new Case();
		String caseToken = (String) token;
		if (caseToken != null) {
			ICaseDao caseDao = new CaseDaoImpl(s);
			try {
				c = caseDao.findCaseByToken(caseToken, version);
			} catch (IllegalArgumentException e) {
				logger.warn("renderCase(String, Integer, ServletResponse)", e);
				return false;
			}
		}
				
		if (c == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("token not found with " + caseToken + ":" + version);
				logger.debug("renderCase(String, Integer, ServletResponse) - end");
			}
			return false;
		}

		IResourceDao dao = new ResourceDaoImpl(s);
		
		/**
		 *Here we assume it's IResource list 
		 */
		List dbResources = dao.listByCase(c.getId());
		JSONArray json = renderResources(dbResources);

		JSONObject jsonres = new JSONObject();
		jsonres.put("token", caseToken);
		jsonres.put("ver", version);
		jsonres.put("resources", json);

		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(jsonres);
		response.setContentType("text/json;charset=UTF-8"); 
		s.close();
		response.getWriter().close();

		if (logger.isDebugEnabled()) {
			logger.debug("renderCase(String, Integer, ServletResponse) - end");
		}
		return true;
	}

	private JSONArray renderResources(List<IResource> dbResources) {
		if (logger.isDebugEnabled()) {
			logger.debug("renderResources(List<Resource>) - start");
		}

		JSONArray json = new JSONArray();
		for (IResource ir : dbResources) {
			
			JSONObject obj = new JSONObject();
			obj.put("type", ir.getType());
			obj.put("name", ir.getName());
			obj.put("content", ir.getFinalContent());
			json.push(obj);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("renderResources(List<Resource>) - end");
		}
		return json;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
}
