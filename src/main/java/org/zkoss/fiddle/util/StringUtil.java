package org.zkoss.fiddle.util;


public class StringUtil {

	public static String concatln(String... arg){
		StringBuffer sb = new StringBuffer();
		for(String str:arg){
			sb.append(str);
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
