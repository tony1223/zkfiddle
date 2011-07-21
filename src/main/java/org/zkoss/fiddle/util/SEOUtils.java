package org.zkoss.fiddle.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.seo.SEOContainer;
import org.zkoss.fiddle.seo.handle.SEOTokenHandlerAdpter;
import org.zkoss.fiddle.seo.model.SEOToken;
import org.zkoss.zk.ui.Desktop;

public class SEOUtils {

	/* TODO: review this */
	public static void render(Desktop desktop, ICase $case) {
		SEOContainer seo = SEOContainer.getInstance(desktop);
		if ($case != null)
			seo.addToken(new SEOToken<ICase>("case", $case));
		
		seo.addHandler(new SEOTokenHandlerAdpter<ICase>() {
			public boolean accept(String type) {
				return "case".equals(type);
			}

			public void resolve(Writer out, String type, ICase model) throws IOException {

				appendTagStart(out, "div", "case");

				appendTitle(out, 2, model.getTitle());
				appendText(out, "version", model.getVersion());
				appendText(out, "token", model.getToken());
				appendText(out, "create date", model.getCreateDate());

				appendTagEnd(out, "div");
			}
		});

	}

	public static void render(Desktop desktop, List<Resource> resources) {
		SEOContainer seo = SEOContainer.getInstance(desktop);

		if (resources != null)
			seo.addToken(new SEOToken<List<Resource>>("resources", resources));

		seo.addHandler(new SEOTokenHandlerAdpter<List<Resource>>() {

			public boolean accept(String type) {
				return "resources".equals(type);
			}

			public void resolve(Writer out, String type, List<Resource> model) throws IOException {

				appendTagStart(out, "div", "resoruces");
				appendTitle(out, 3, "resources");

				for (Resource r : model) {
					appendText(out, "fileName", r.getName());
					appendText(out, "fileType", r.getTypeName());
					// r.getFinalContent() == null only when default resources
					appendText(out, "fileContent", r.getFinalContent() == null ? r.getContent() : r.getFinalContent());
				}
				appendTagEnd(out, "div");
			}
		});
	}
}
