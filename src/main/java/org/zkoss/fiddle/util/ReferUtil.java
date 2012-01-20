package org.zkoss.fiddle.util;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

public class ReferUtil {
	public static String getRefer(Execution exec){
		String ref = Executions.getCurrent().getHeader("Referer");
		String hostname = Executions.getCurrent().getServerName();
		System.out.println("Referer:"+Executions.getCurrent().getHeader("Referer")+":"+Executions.getCurrent().getServerName());
		
		if(ref != null){
			if(ref.indexOf(hostname) != -1){
				return ref;
			}
		}

		return ref;
	}
	

	public static String getRefer(HttpServletRequest request){
		String ref = request.getHeader("Referer");
		String hostname =request.getServerName();
		
		if(ref != null  && (ref.indexOf(hostname) == -1)){
			return ref;
		}

		return null;
	}

	
}
