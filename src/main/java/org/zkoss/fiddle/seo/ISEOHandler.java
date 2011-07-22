package org.zkoss.fiddle.seo;

import java.io.IOException;
import java.io.Writer;

public interface ISEOHandler<T> {

	public abstract T getModel();

	public abstract void setModel(T model);

	public abstract void process(Writer out) throws IOException;

	public abstract void resolve(Writer out, T model) throws IOException;

}