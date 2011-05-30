package org.zkoss.fiddle.util;

import java.util.zip.CRC32;

public class CRCCaseIDEncoder implements ICaseIDEncoder {

	public String encode(Long id) {
		CRC32 crc = new CRC32();
		crc.update(("" + id).getBytes());
		return Long.toString(crc.getValue(), 32);
	}
	
	private static CRCCaseIDEncoder instance;
	
	public static CRCCaseIDEncoder getInstance(){
		if(instance == null )  instance = new CRCCaseIDEncoder();
		return instance;
	}
	


}
