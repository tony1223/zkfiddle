package org.zkoss.fiddle.model.api;

import java.util.Date;

/**
 * Used for same rendering case,
 * it's the same interface between CaseRecord and Case, 
 * 
 * @author tony
 *
 */
public interface IRenderCase {

	public Integer getVersion();

	public String getToken();

	public String getTitle();
	
	public String getURLFriendlyTitle() ;
	
	public String getCaseUrl();
	
	public Date getCreateDate();
	
}
