package org.zkoss.fiddle.seo;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.zkoss.fiddle.seo.handle.DefaultSEOTokenHandler;
import org.zkoss.fiddle.seo.handle.SEOTokenHandler;
import org.zkoss.fiddle.seo.model.SEOToken;
import org.zkoss.zk.ui.Desktop;

/**
 * Used in FiddleSEORenderer Each execution should only have one SEOContainer,
 * and only the first execution will be enabled , if you put SEOContainer in a
 * ZKAU Request , it's meaning less.
 * 
 * @author tony
 * 
 */
public class SEOContainer {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SEOContainer.class);

	private List<SEOToken> tokens;

	private List<SEOTokenHandler> handlers;

	private static SEOTokenHandler handler = new DefaultSEOTokenHandler();

	private static final String attr = "__seoHandler";

	/**
	 * if a SEO Container is rendered , we just drop items and then archive it
	 * ...
	 */
	private boolean isAchived = false;

	public SEOContainer() {
		tokens = new ArrayList<SEOToken>();
		handlers = new ArrayList<SEOTokenHandler>();
	}

	private void doHandleSEO(Writer out, SEOToken token) {

		for (SEOTokenHandler handler : handlers) {
			if (handler.accept(token.getType())) {
				try {
					handler.resolve(out, token.getType(), token.getModel());
				} catch (IOException e) {
					if (logger.isEnabledFor(Level.ERROR)) {
						logger.error("resolve(Writer, T)", e);
					}
				}

				return;
			}
		}

		try {
			handler.resolve(out, token.getType(), token.getModel());
		} catch (IOException e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error("resolve(Writer, T)", e);
			}
		}
	}

	public void addHandler(SEOTokenHandler handler) {
		handlers.add(handler);
	}

	/**
	 * Archive data , to prevent some user add a lot of data after page is
	 * rendered , that's meaning less.
	 */
	public void process(Writer out) {

		for (SEOToken token : tokens) {
			doHandleSEO(out, token);
		}

		isAchived = true;
		tokens = null;
	}

	public List<SEOToken> getTokens() {
		if (!isAchived) {
			return Collections.unmodifiableList(tokens);
		} else {
			return null;
		}
	}

	public void addToken(SEOToken token) {
		if (!isAchived) {
			tokens.add(token);
		} else {
			if (logger.isEnabledFor(Level.WARN)) {
				logger.warn(
						"addToken(SEOToken) - SEO Container is archived , it will not take any effect to add a SEO Token. - token="
								+ token, null);
			}
		}
	}

	public static SEOContainer getInstance(Desktop exe) {
		if (exe == null) {
			throw new IllegalArgumentException("execution can't be null ");
		}
		if (exe.hasAttribute(attr)) {
			if (!(exe.getAttribute(attr) instanceof SEOContainer)) {
				throw new IllegalStateException("It might be a naming conflict for " + attr + ", type not matching ");
			}
			return (SEOContainer) exe.getAttribute(attr);
		} else {
			SEOContainer seoc = new SEOContainer();
			exe.setAttribute(attr, seoc);
			return seoc;
		}
	}

	public boolean isAchived() {
		return isAchived;
	}
}
