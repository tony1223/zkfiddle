package org.zkoss.fiddle.core.utils;

import java.util.zip.CRC32;


public class CRCCaseIDEncoder implements ICaseIDEncoder {

	public String encode(String string) {
		CRC32 crc = new CRC32();
		crc.update((string).getBytes());
		return Long.toString(crc.getValue(), 32);
	}

	public String encode(Long id) {
		CRC32 crc = new CRC32();
		crc.update(("" + id).getBytes());
		return Long.toString(crc.getValue(), 32);
	}

	private static CRCCaseIDEncoder instance;

	public static CRCCaseIDEncoder getInstance() {
		if (instance == null)
			instance = new CRCCaseIDEncoder();
		return instance;
	}

}
