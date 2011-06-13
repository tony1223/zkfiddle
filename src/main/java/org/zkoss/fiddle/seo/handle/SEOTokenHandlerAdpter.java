package org.zkoss.fiddle.seo.handle;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;
import org.zkoss.lang.Objects;

public abstract class SEOTokenHandlerAdpter<T> implements SEOTokenHandler<T> {

	/**
	 * Key will be used as a css class ,please follow css class's rule.
	 * @param out
	 * @param key
	 * @param text
	 * @throws IOException
	 */
	public void appendText(Writer out, String key, Object text) throws IOException {
		
		appendTagStart(out,"span",StringEscapeUtils.escapeHtml(key));
		out.append(	StringEscapeUtils.escapeHtml(Objects.toString(text)));
		appendTagEnd(out,"span");
	}

	public void appendText(Writer out, Object text) throws IOException {
		
		appendTagStart(out,"span");
		out.append(StringEscapeUtils.escapeHtml(Objects.toString(text)));
		appendTagEnd(out,"span");
	}

	public void appendTitle(Writer out, int level, String title) throws IOException {
		appendTagStart(out,"h" + level);
		out.append(StringEscapeUtils.escapeHtml(title));
		appendTagEnd(out,"h" + level);
	}

	public void appendTagStart(Writer out,String tag) throws IOException {
		appendTagStart(out,tag,null);
	}
	
	
	public void appendTagStart(Writer out,String tag,String cls) throws IOException {
		out.append("<" + tag + (cls == null ? ">":" class=\""+cls+"\">") );
	}
	
	public void appendTagEnd(Writer out,String tag) throws IOException {
		out.append("</" + tag + ">" );
	}
}
