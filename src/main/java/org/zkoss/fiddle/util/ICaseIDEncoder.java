package org.zkoss.fiddle.util;


public interface ICaseIDEncoder {
	
	public String encode(Long id);
	
	public Long decode(String val);
}
