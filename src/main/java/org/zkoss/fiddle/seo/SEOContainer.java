package org.zkoss.fiddle.seo;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Desktop;

/**
 * Used in FiddleSEORenderer Each execution should only have one SEOContainer,
 * and only the first execution will be enabled , if you put SEOContainer in a
 * ZKAU Request , it's meaning less.
 * 
 * @author tony
 * 
 */
@SuppressWarnings("rawtypes")
public class SEOContainer {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SEOContainer.class);
	
	private static final String attr = "__seoHandler";
	
	private List<ISEOHandler> items;

	/**
	 * if a SEO Container is rendered , we just drop items and then archive it
	 * ...
	 */
	private boolean isAchived = false;

	public SEOContainer() {
		items = new ArrayList<ISEOHandler>();
	}

	/**
	 * Archive data , to prevent some user add a lot of data after page is
	 * rendered , that's meaning less.
	 */
	public void process(Writer out) {

		for (ISEOHandler token : items) {
			try {
				token.process(out);
			} catch (IOException e) {
				if (logger.isEnabledFor(Level.ERROR))
					logger.error("SEO processing ERROR:",e);
			}
		}

		isAchived = true;
		items = null;
	}

	public List<ISEOHandler> getTokens() {
		if (!isAchived) {
			return Collections.unmodifiableList(items);
		} else {
			return null;
		}
	}

	public void addHandler(ISEOHandler token) {
		if (!isAchived) {
			items.add(token);
		} else {
			if (logger.isEnabledFor(Level.WARN)) {
				logger.warn(
						"addToken(SEOToken) - SEO Container is archived , it will not take any effect to add a SEO Token. - token="
								+ token, null);
			}
		}
	}

	public static SEOContainer getInstance(Desktop desktop) {
		if (desktop == null) {
			throw new IllegalArgumentException("execution can't be null ");
		}
		if (desktop.hasAttribute(attr)) {
			if (!(desktop.getAttribute(attr) instanceof SEOContainer)) {
				throw new IllegalStateException("It might be a naming conflict for " + attr + ", type not matching ");
			}
			return (SEOContainer) desktop.getAttribute(attr);
		} else {
			SEOContainer seoc = new SEOContainer();
			desktop.setAttribute(attr, seoc);
			return seoc;
		}
	}

	public boolean isAchived() {
		return isAchived;
	}
}
