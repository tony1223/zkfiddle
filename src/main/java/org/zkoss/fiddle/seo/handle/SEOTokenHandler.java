package org.zkoss.fiddle.seo.handle;

import java.io.IOException;
import java.io.Writer;

public interface SEOTokenHandler<T>{

	public boolean accept(String type);
	
	public void resolve(Writer out,String type,T model) throws IOException ;
	
}
