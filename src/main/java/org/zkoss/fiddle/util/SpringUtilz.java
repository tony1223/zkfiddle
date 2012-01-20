package org.zkoss.fiddle.util;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.spring.SpringUtil;

public class SpringUtilz {

	/**
	 * Get the spring application context.
	 */
	public static ApplicationContext getApplicationContext(ServletContext context) {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(context);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name){
		return (T) SpringUtil.getBean(name);
	}
	
	/**
	 * Get the spring bean by the specified name.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(ServletContext context, String name) {
		Object o = null;
		try {
			if (getApplicationContext(context).containsBean(name)) {
				o = getApplicationContext(context).getBean(name);
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// ignore
		}
		return (T) o;
	}

	/**
	 * Get the spring bean by the specified name and class.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T  getBean(ServletContext context, String name, @SuppressWarnings("rawtypes") Class cls) {
		Object o = null;
		try {
			if (getApplicationContext(context).containsBean(name)) {
				o = getApplicationContext(context).getBean(name, cls);
			}
		} catch (BeanNotOfRequiredTypeException e) {
			// ignore
		}
		return (T)o;
	}

}
