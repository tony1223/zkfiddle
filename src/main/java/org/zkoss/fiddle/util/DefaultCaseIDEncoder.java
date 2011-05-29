package org.zkoss.fiddle.util;


/**
 * It's default , means do nothing. haha
 * @author tony
 *
 */
public class DefaultCaseIDEncoder implements ICaseIDEncoder{

	private static DefaultCaseIDEncoder instance;
	
	public String encode(Long id) {
		return String.valueOf(id);
	}

	public Long decode(String val) {
		try{
			return Long.parseLong(val);
		}catch(NumberFormatException e){
			//TODO wrote a logger here.
			throw new IllegalArgumentException("val is not correct:"+val);
		}
	}

	public static DefaultCaseIDEncoder getInstance(){
		if(instance != null )  instance = new DefaultCaseIDEncoder();
		return instance;
	}
}
