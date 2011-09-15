package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.visualmodel.UserVO;


public class SiteTitleUtils {
	public static String getTitle(String subtitle) {
		return "ZK Fiddle - "+ subtitle;
	}

	public static String getTitle(UserVO userVO){
		return  getTitle("User - "+ userVO.getUserName());
	}

	public static String getTitle(Tag tag){
		return  getTitle("Tag "+ tag.getName());
	}

}
