package org.zkoss.fiddle.core.utils;



/**
 * It's default , means do nothing. haha
 * @author tony
 *
 */
public class DefaultCaseIDEncoder implements ICaseIDEncoder{

	private static DefaultCaseIDEncoder instance;
	
	public static DefaultCaseIDEncoder getInstance(){
		if(instance == null )  instance = new DefaultCaseIDEncoder();
		return instance;
	}
	
	public String encode(Long id) {
		return String.valueOf(id);
	}

}
