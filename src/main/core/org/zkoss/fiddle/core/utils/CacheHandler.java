package org.zkoss.fiddle.core.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

/**
 * A cache implementation to wrap ehcache.
 *
 * Need to review if we have better approach to handle this , at least we could
 * use the method from spring.
 *
 * @author tony
 *
 */
public abstract class CacheHandler<T> {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CacheHandler.class);

	/**
	 * the actually query process
	 * @return
	 */
	abstract protected T execute();

	/**
	 * how to get the key.
	 * @return
	 */
	abstract protected String getKey();

	/**
	 * Get data from cache if exist , or we will add it to cache.
	 * @param cache
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T get(Cache cache) {
		String key = getKey();
		if (cache.isKeyInCache(key)) {
			if (logger.isDebugEnabled()) {
				logger.debug("CacheCallback:[" + cache.getName() + "] - Hit cache [" + key + "]");
			}

			return (T) cache.get(key).getValue();
		} else {
			T ret = execute();
			cache.put(new Element(key, ret));
			return ret;
		}
	}


}
