package org.zkoss.fiddle.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * A cache implementation to wrap ehcache.
 * 
 * Note , you have to cehck ehcache.xml if you need build new cache .
 * @author tony
 * 
 */
public enum FiddleCache {
	CaseResources,
	RecentlyCases,
	CaseByToken,
	top10liked;
	
	public Cache getInstance(){
		return CacheManager.getInstance().getCache(this.name());
	}
	
	public void removeAll(){
		getInstance().removeAll();
	}
}
