package org.zkoss.fiddle.seo.model;

public class SEOToken<T> {

	public String type;

	public T model;

	public SEOToken(String type, T model) {
		this.model = model;
		this.type= type;
	}

	
	public String getType() {
		return type;
	}

	
	public void setType(String type) {
		this.type = type;
	}

	
	public T getModel() {
		return model;
	}

	
	public void setModel(T model) {
		this.model = model;
	}
}
