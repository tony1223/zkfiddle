package org.zkoss.fiddle.model;

import org.zkoss.fiddle.model.api.IResource;


public class Resource implements IResource {
	private String name;
	private String content;
	private Integer type ;
	
	public Resource(int pType){
		this(pType,null,null);
	}
	
	public Resource(int pType,String pName,String pContent){
		type = pType;
		name = pName;
		content = pContent;
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

	public Integer getType() {
		return type;
	}

	public String getTypeName() {
		switch(type){
			case 0: return "zul";
			case 1: return "java";
			case 2: return "javascript";
			case 3: return "html";
			case 4: return "css";
			default: return "unknown";
		}
	}

	public String getTypeMode() {
		switch(type){
			case 0: return "xml";
			case 1: return "java";
			case 2: return "javascript";
			case 3: return "html";
			case 4: return "css";
			default: return "unknown";
		}
	}
	
}
