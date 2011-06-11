package org.zkoss.fiddle.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * A cache implementation to wrap ehcache.
 * 
 * Need to review if we have better approach to handle this , 
 * at least we could use the method from spring.
 * @author tony
 * 
 */
public class CacheFactory {

	public static Cache getCaseResources() {
		CacheManager cm = CacheManager.getInstance();
		return cm.getCache("CaseResources");
	}
	
	public static Cache getRecentlyCases() {
		CacheManager cm = CacheManager.getInstance();
		return cm.getCache("RecentlyCases");
	}
	
	public static Cache getCaseByToken() {
		CacheManager cm = CacheManager.getInstance();
		return cm.getCache("CaseByToken");
	}
	
	
	public static Cache getTop10LikedRecord() {
		CacheManager cm = CacheManager.getInstance();
		return cm.getCache("top10liked");
	}
	
}
