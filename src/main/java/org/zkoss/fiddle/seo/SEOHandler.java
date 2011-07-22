package org.zkoss.fiddle.seo;

import java.io.IOException;
import java.io.Writer;

public abstract class SEOHandler<T> implements ISEOHandler<T> {

	public T model;

	public SEOHandler(T model) {
		this.model = model;
	}
	
	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.seo.model.ISEOHandler#getModel()
	 */
	@Override
	public T getModel() {
		return model;
	}

	
	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.seo.model.ISEOHandler#setModel(T)
	 */
	@Override
	public void setModel(T model) {
		this.model = model;
	}
	
	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.seo.model.ISEOHandler#process(java.io.Writer)
	 */
	@Override
	public void process(Writer out) throws IOException {
		resolve(out,model);
	}
	
	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.seo.model.ISEOHandler#resolve(java.io.Writer, T)
	 */
	@Override
	public abstract void resolve(Writer out,T model) throws IOException ;
}
