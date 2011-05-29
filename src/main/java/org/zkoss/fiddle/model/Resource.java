package org.zkoss.fiddle.model;

import org.zkoss.fiddle.model.api.IResource;


public class Resource implements IResource {
	private Long id;
	private String name;
	private String content;
	private Integer type ;
	private Boolean isModified;
	
	public Resource(int pType){
		this(pType,null,null);
	}
	
	public Resource(int pType,String pName,String pContent){
		type = pType;
		name = pName;
		content = pContent;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

	public String getTypeName() {
		switch(type){
			case TYPE_ZUL: return "zul";
			case TYPE_JAVA: return "java";
			case TYPE_JS: return "javascript";
			case TYPE_HTML: return "html";
			case TYPE_CSS: return "css";
			default: return "unknown";
		}
	}

	public String getTypeMode() {
		switch(type){
			case TYPE_ZUL: return "xml";
			case TYPE_JAVA: return "java";
			case TYPE_JS: return "javascript";
			case TYPE_HTML: return "html";
			case TYPE_CSS: return "css";
			default: return "unknown";
		}
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean enb) {
		isModified = enb;
	}
	
}
