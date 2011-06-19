package org.zkoss.fiddle.filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.files.ResourceFile;
import org.zkoss.fiddle.files.ResourcePackager;
import org.zkoss.fiddle.files.ZipFileUtil;
import org.zkoss.fiddle.manager.VirtualCaseManager;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.VirtualCase;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

public class FiddleDataFilter implements Filter {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FiddleDataFilter.class);

	private ServletContext servletContext;
	
	private ICaseDao caseDao;

	private IResourceDao resourceDao;
	
	private ICaseRecordDao caseRecordDao;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - start");
		}
			

		HttpServletRequest httprequest = ((HttpServletRequest) request);
		
		String uri = httprequest.getRequestURI();
		String context = httprequest.getContextPath();

		String path = uri.replaceFirst(context, "");

		Matcher match = null;

		// TODO find a time to review this
		boolean download = false;
		if (path.indexOf("/data") != -1) {
			Pattern pattern = Pattern.compile("/data/([^/]+)(/(.*))?");
			match = pattern.matcher(path);
		} else {
			download = true;
			Pattern pattern = Pattern.compile("/download/([^/]+)(/(.*))?");
			match = pattern.matcher(path);
		}

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

		if (token == null)
			return;

		if(download){
			//TODO review
			servletContext = ((HttpServletRequest)request).getSession().getServletContext();
			outputZIP( (HttpServletResponse) response, token, version);
		}else
			outputJSON(response, token, version);

		if (logger.isDebugEnabled()) {
			logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - end");
		}
	}

	private void outputJSON(ServletResponse response, String token, Integer version) throws IOException {
		try {

			boolean result = false;
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			if (version != 0) {
				result = renderCase(token, version, response);
			} else {
				result = renderVisualCase(token, version, response);
			}

			if (!result) { // means failed when writing resource/case
				response.getWriter().println("false");
				response.getWriter().close();
			}

		} catch (Exception ex) {
			logger.error("doFilter(ServletRequest, ServletResponse, FilterChain)", ex);

			response.getWriter().println("false");
			response.getWriter().close();
		}

	}
	
	private void outputZIP(HttpServletResponse response, String token, Integer version) throws IOException {
		try {
			response.setContentType("application/zip");
		
			String fileName="fiddle-"+token+"-ver-"+version+".zip";
			response.setHeader("content-disposition",  "attachment; filename=" + fileName );

			
			//In download case we always load saved case but not virtual cases
			ByteArrayOutputStream baos = getZipedCaseResources(token, version, response);
			response.setContentLength(baos.size());
			response.getOutputStream().write(baos.toByteArray());
			response.getOutputStream().close();

		} catch (Exception ex) {
			response.getOutputStream().close();
		}

	}

	private boolean renderVisualCase(String token, Integer version, ServletResponse response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("renderVisualCase(String, Integer, ServletResponse) - start");
		}

		VirtualCase vc = VirtualCaseManager.getInstance().find(token);
		if (vc == null) {
			return false; // "virtual case not found"
		}
		JSONArray json = renderResources(vc.getResources());

		JSONObject jsonres = new JSONObject();
		jsonres.put("token", token);
		jsonres.put("resources", json);

		response.getWriter().println(jsonres);
		response.getWriter().close();

		if (logger.isDebugEnabled()) {
			logger.debug("renderVisualCase(String, Integer, ServletResponse) - end");
		}
		return true;
	}

	private ByteArrayOutputStream getZipedCaseResources(String token, Integer version, ServletResponse response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("renderCase(String, Integer, ServletResponse) - start");
		}

		// we have to open a one because we are in filter :(

		Case c = null; // new Case();
		String caseToken = (String) token;
		if (caseToken != null) {
			try {
				c = caseDao.findCaseByToken(caseToken, version);
			} catch (IllegalArgumentException e) {
				logger.warn("renderCase(String, Integer, ServletResponse)", e);
				return null;
			}
		}

		if (c == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("token not found with " + caseToken + ":" + version);
				logger.debug("renderCase(String, Integer, ServletResponse) - end");
			}
			return null;
		}
		
		caseRecordDao.increase(CaseRecord.Type.Download, c);
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		/**
		 * Here we assume it's IResource list
		 */
		List<Resource> dbResources = resourceDao.listByCase(c.getId());
		
		ResourcePackager list = ResourcePackager.list();
		String filePath = servletContext.getRealPath("/WEB-INF/_download/ZKfiddleSample.zip");
		
		list.add(ZipFileUtil.getFiles(new File(filePath)));
		for(IResource ir:dbResources){
			list.add(new ResourceFile(ir));
		}
		list.export(ret);
		return ret;
	}
	private boolean renderCase(String token, Integer version, ServletResponse response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("renderCase(String, Integer, ServletResponse) - start");
		}

		// we have to open a one because we are in filter :(

		Case c = null; // new Case();
		String caseToken = (String) token;
		if (caseToken != null) {
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

		/**
		 * Here we assume it's IResource list
		 */
		List dbResources = resourceDao.listByCase(c.getId());
		JSONArray json = renderResources(dbResources);

		JSONObject jsonres = new JSONObject();
		jsonres.put("token", caseToken);
		jsonres.put("ver", version);
		jsonres.put("resources", json);

		response.getWriter().println(jsonres);
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
		servletContext =filterConfig.getServletContext();
	}

	public void destroy() {
	}

	public void setCaseDao(ICaseDao caseDao) {
		this.caseDao = caseDao;
	}

	public void setResourceDao(IResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	
	public void setCaseRecordDao(ICaseRecordDao caseRecordDao) {
		this.caseRecordDao = caseRecordDao;
	}

}
