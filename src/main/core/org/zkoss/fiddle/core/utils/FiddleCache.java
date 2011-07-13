package org.zkoss.fiddle.core.utils;

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
	CaseTag,
	Top10liked;
	
	public Cache getInstance(){
		return CacheManager.getInstance().getCache(this.name());
	}


	public void removeAll(){
		getInstance().removeAll();
	}
	
	public void remove(String key){
		getInstance().remove(key);
	}

	public <H> H execute(CacheHandler<H> handle){
		return handle.get(getInstance());		
	}
}
