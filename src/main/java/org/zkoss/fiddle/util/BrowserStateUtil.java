package org.zkoss.fiddle.util;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.viewmodel.URLData;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.visualmodel.UserVO;


public class BrowserStateUtil {

	public static void go(Tag tag){
		BrowserState.go(TagUtil.getViewURL(tag), SiteTitleUtils.getTitle(tag),
				new URLData(FiddleConstant.URL_DATA_TAG_VIEW, tag));
	}

	public static void go(Case theCase){
		BrowserState.go(CaseUtil.getSampleURL(theCase),
				 SiteTitleUtils.getTitle(CaseUtil.getPublicTitle(theCase)),
				 new URLData(FiddleConstant.URL_DATA_CASE_VIEW, theCase));
	}

	public static void go(UserVO userVO){
		BrowserState.go(UserUtil.getUserView(userVO),
				SiteTitleUtils.getTitle(userVO),
				new URLData(FiddleConstant.URL_DATA_USER_VIEW,userVO));
	}
}
