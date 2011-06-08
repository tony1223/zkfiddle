package org.zkoss.fiddle.model.api;

import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Resource;

public interface IResource{

	public static int TYPE_ZUL = 0;

	public static int TYPE_JAVA = 1; // actually it's beanshell

	public static int TYPE_JS = 2;

	public static int TYPE_HTML = 3;

	public static int TYPE_CSS = 4;

	public static final String PACKAGE_TOKEN = "pkg$";

	public static final String PACKAGE_TOKEN_ESCAPE = "pkg\\$";

	public static final String PACKAGE_PREFIX = "";

	public Long getId();

	public void setId(Long id);

	public void setName(String name);

	public String getName();

	public void setContent(String content);

	public String getContent();

	public void setType(Integer type);

	public Integer getType();

	public String getTypeName();

	public String getTypeMode();

	public Long getCaseId();

	public void setCaseId(Long caseId);

	public void setCanDelete(boolean canDelete);

	public boolean isCanDelete();

	public Resource clone();

	public void setPkg(String pkg);

	public String getPkg();

	public void setFinalContent(String rawContent);

	public String getFinalContent();
	
	public void buildFinalConetnt(Case c) ;
	
}
