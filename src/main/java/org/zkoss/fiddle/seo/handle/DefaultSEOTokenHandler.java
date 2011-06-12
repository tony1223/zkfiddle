package org.zkoss.fiddle.seo.handle;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.lang.Objects;

public class DefaultSEOTokenHandler<T> extends SEOTokenHandlerAdpter<T> {

	public boolean accept(String type) {
		return true;
	}

	public void resolve(Writer out, String type, T model) throws IOException {

		appendText(out, type, Objects.toString(model));
	}

}
