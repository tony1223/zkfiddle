package org.zkoss.fiddle.model.api;

import java.util.Date;

public interface ICase {

	public Long getId();

	public void setId(Long id);

	public Long getThread();

	public void setThread(Long thread);

	public Integer getVersion();

	public void setVersion(Integer version);

	public String getToken();

	public void setToken(String token);

	public abstract void setCreateDate(Date createDate);

	public abstract Date getCreateDate();

	public abstract void setFromId(Long from);

	public abstract Long getFromId();
	
	public String getTitle();
	
	public void setTitle(String title) ;
	
	public String getURLFriendlyTitle() ;
	
	public String getCaseUrl();
	
	public String getCaseUrl(String ver);
	
	public String getPosterIP() ;
	
	public void setPosterIP(String posterIP) ;

}
