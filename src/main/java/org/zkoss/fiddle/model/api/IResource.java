package org.zkoss.fiddle.model.api;


public interface IResource {
	public static int TYPE_ZUL 	= 0;
	public static int TYPE_JAVA = 1; //actually it's beanshell  
	public static int TYPE_JS 	= 2;
	public static int TYPE_HTML = 3;
	public static int TYPE_CSS 	= 4;
	
	
	public void setName(String name);
	public String getName();
	
	public void setContent(String content);
	public String getContent();
	
	public Integer getType();
	
	public String getTypeName();
	
	public String getTypeMode();

}