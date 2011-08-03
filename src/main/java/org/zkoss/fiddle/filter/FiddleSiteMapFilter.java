package org.zkoss.fiddle.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.zkoss.fiddle.core.utils.CacheHandler;
import org.zkoss.fiddle.core.utils.FiddleCache;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.fiddle.util.SpringUtilz;


public class FiddleSiteMapFilter implements Filter {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FiddleSiteMapFilter.class);

	private ServletContext ctx;


	public static long latestUpdate = -1;
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		String sitemapString = FiddleCache.SiteMap.execute(new CacheHandler<String>() {
			protected String execute() {
				ICaseDao caseDao = (ICaseDao) SpringUtilz.getBean(ctx, "caseDao");
				return renderSiteMap(caseDao.list());
			}
			protected String getKey() {
				return "sitemap";
			}
		});
		if (logger.isInfoEnabled()) {
			logger.info("Print SiteMap.");
		}
		response.getWriter().write(sitemapString);
	}

	private String renderSiteMap(List<Case> list){
		if (logger.isInfoEnabled()) {
			logger.info("Loading SiteMap From DB, total items :"+list.size());
		}
		
		String domain = FiddleConfig.getHostName();
		StringBuffer sb= new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" >\n");

		for(Case _case:list){
			sb.append("<url>");
			sb.append("<loc>");
			sb.append(domain+CaseUtil.getSampleURL(_case));
			sb.append("</loc>");
			sb.append("</url>");
		}
		sb.append("</urlset>");

		return sb.toString();

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
	}

	public void destroy() {
	}

}
