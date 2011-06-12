package org.zkoss.fiddle.seo;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.SEORenderer;



public class FiddleSEORenderer  implements SEORenderer {
		public void render(Page page, Writer out) throws IOException {

		SEOContainer soc = SEOContainer.getInstance(page.getDesktop());
		soc.getTokens();
		soc.process(out);
		
	}

}
