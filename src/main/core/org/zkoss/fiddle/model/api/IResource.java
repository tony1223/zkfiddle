package org.zkoss.fiddle.model.api;

import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Media;
import org.zkoss.fiddle.model.Resource;

@Deprecated
public interface IResource{

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

	public String buildFinalConetnt(String replacedPackage);
	
	public String getFinalContent();
	
	public void setFinalConetnt(Case c) ;
	
	public String getFullPackage();
	
	public Media getMedia();
	
	public void setMedia(Media media);
	
}
