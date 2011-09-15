package org.zkoss.fiddle.filter;

import java.io.IOException;
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
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.web.servlet.Servlets;

public class FiddleUserFilter extends FiddleViewBaseFilter {

	private static final String FIDDLE_HOST_NAME = "fiddleHostName";

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FiddleUserFilter.class);

	private Pattern tagPattern = Pattern.compile("^/user/(guest/)?(.*?)(;jsessionid=.*)?$");

	public void doFilter(ServletRequest request2, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");

		Matcher m = tagPattern.matcher(path);
		if(m.find()){

			super.doFilter(request, response, chain);

			request.setAttribute(FiddleConstant.REQUEST_ATTR_CONTENT_PAGE, FiddleConstant.REQUEST_VALUE_PAGE_TYPE_USER);
			final boolean guest = (m.group(1) != null);
			final String userName = URLDecoder.decode(m.group(2),  FiddleConstant.CHARSET_UTF_8);

			request.setAttribute(FiddleConstant.REQUEST_ATTR_PAGE_TITLE," - User - "+ userName);
			request.setAttribute(FiddleConstant.REQUEST_ATTR_USERNAME,userName);
			request.setAttribute(FiddleConstant.REQUEST_ATTR_GUEST,guest);

			request.setAttribute(FIDDLE_HOST_NAME, FiddleConfig.getHostName());

			if (logger.isDebugEnabled()) {
				logger.debug("[FiddleUserFilter::doFilter]User Name=" + m.group(1));
			}

			Servlets.forward(ctx, request, response, "/WEB-INF/_include/index.zul" );

		}else
			chain.doFilter(request2, response);

	}


}
