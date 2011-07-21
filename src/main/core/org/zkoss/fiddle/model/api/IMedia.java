/**IMedia.java
 * 2011/7/15
 * 
 */
package org.zkoss.fiddle.model.api;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface IMedia {
	
	public Long getId();
	public void setId(Long id);
	
	byte[] getContent();
	
	void setContent(byte[] binary);
}
