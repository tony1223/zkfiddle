package org.zkoss.fiddle.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.IRenderCase;
import org.zkoss.fiddle.seo.SEOContainer;
import org.zkoss.fiddle.seo.SEOHandler;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Desktop;

public class SEOUtils {

	public static void render(Desktop desktop,final String listTitle, List<? extends IRenderCase> list) {
		SEOContainer seo = SEOContainer.getInstance(desktop);
		if (listTitle != null) {
			seo.addHandler(new SEOHandler<String>(listTitle) {
				public void resolve(Writer out, String model) throws IOException {
					appendTitle(out, 2, listTitle);
				}
			});
		}
		for(IRenderCase _case:list){
			SEOUtils.render(desktop, _case);
		}
	}
	
	public static void render(Desktop desktop, IRenderCase pCase) {
		SEOContainer seo = SEOContainer.getInstance(desktop);
		if (pCase != null) {
			seo.addHandler(new SEOHandler<IRenderCase>(pCase) {

				public void resolve(Writer out, IRenderCase item) throws IOException {
					appendTagStart(out, "div", "case");
					appendTitle(out, 2, item.getTitle());
					appendText(out, "version", item.getVersion());
					appendText(out, "token", item.getToken());
					
					if(item.getCreateDate() != null){
						appendText(out, "create date", item.getCreateDate());
					}
					
					appendLink(out, CaseUtil.getSampleURL(item), "link");
					appendTagEnd(out, "div");
				}
			});
		}
	}

	public static void render(Desktop desktop, List<Resource> resources) {
		SEOContainer seo = SEOContainer.getInstance(desktop);

		if (resources != null) {
			seo.addHandler(new SEOHandler<List<Resource>>(resources) {

				public void resolve(Writer out, List<Resource> model) throws IOException {
					appendTagStart(out, "div", "resoruces");
					appendTitle(out, 3, "resources");

					for (Resource r : model) {
						appendText(out, "fileName", r.getName());
						appendText(out, "fileType", r.getTypeName());
						appendText(out, "fileContent",
								r.getFinalContent() == null ? r.getContent() : r.getFinalContent());
					}
					appendTagEnd(out, "div");
				}
			});
		}

	}

	/**
	 * Key will be used as a css class ,please follow css class's rule.
	 * 
	 * @param out
	 * @param key
	 * @param text
	 * @throws IOException
	 */
	public static void appendText(Writer out, String key, Object text) throws IOException {

		appendTagStart(out, "span", StringEscapeUtils.escapeHtml(key));
		out.append(StringEscapeUtils.escapeHtml(Objects.toString(text)));
		appendTagEnd(out, "span");
	}

	public static void appendLink(Writer out,String href,String label) throws IOException {
		out.append("<a href=\"" + href + "\">" + StringEscapeUtils.escapeHtml(label) + "</a>");
	}
	
	public static void appendText(Writer out, Object text) throws IOException {

		appendTagStart(out, "span");
		out.append(StringEscapeUtils.escapeHtml(Objects.toString(text)));
		appendTagEnd(out, "span");
	}

	public static void appendTitle(Writer out, int level, String title) throws IOException {
		appendTagStart(out, "h" + level);
		out.append(StringEscapeUtils.escapeHtml(title));
		appendTagEnd(out, "h" + level);
	}

	public static void appendTagStart(Writer out, String tag) throws IOException {
		appendTagStart(out, tag, null);
	}

	public static void appendTagStart(Writer out, String tag, String cls) throws IOException {
		out.append("<" + tag + (cls == null ? ">" : " class=\"" + cls + "\">"));
	}

	public static void appendTagEnd(Writer out, String tag) throws IOException {
		out.append("</" + tag + ">");
	}
}
