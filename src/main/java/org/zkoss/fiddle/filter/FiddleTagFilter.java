package org.zkoss.fiddle.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.dao.api.ITagDao;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.fiddle.util.SpringUtilz;
import org.zkoss.web.servlet.Servlets;

public class FiddleTagFilter extends FiddleViewBaseFilter {


	private static final String FIDDLE_HOST_NAME = "fiddleHostName";

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FiddleTagFilter.class);

	private Pattern tagPattern = Pattern.compile("^/tag/(.*?)(;jsessionid=.*)?$");

	public void doFilter(ServletRequest request2, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");

		Matcher m = tagPattern.matcher(path);
		if(m.find()){
			super.doFilter(request2, response, chain);

			request.setAttribute(FiddleConstant.REQUEST_ATTR_CONTENT_PAGE, FiddleConstant.REQUEST_VALUE_PAGE_TYPE_TAG);
			final Tag t = getTag(m.group(1));
			if( t == null ){
				chain.doFilter(request2, response);
				return ;
			}

			request.setAttribute(FiddleConstant.REQUEST_ATTR_PAGE_TITLE," - Tag - "+ t.getName());
			request.setAttribute(FiddleConstant.REQUEST_ATTR_TAG,t);
			request.setAttribute(FIDDLE_HOST_NAME, FiddleConfig.getHostName());

			if (logger.isDebugEnabled()) {
				logger.debug("[FiddleTagFilter::doFilter]Tag Name=" + m.group(1));
			}

			Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul" );

		}else
			chain.doFilter(request2, response);

	}

	private Tag getTag(String mtag) throws UnsupportedEncodingException{
		if(mtag == null || "".equals(mtag.trim())){
			return null;
		}

		ITagDao tagDao = (ITagDao) SpringUtilz.getBean(ctx,"tagDao");
		String tagName = URLDecoder.decode(mtag, FiddleConstant.CHARSET_UTF_8);
		return tagDao.getTag(tagName);

	}

}
