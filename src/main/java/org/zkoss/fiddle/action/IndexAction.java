package org.zkoss.fiddle.action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.zest.ActionContext;
import org.zkoss.zest.ParameterIgnored;


public class IndexAction implements ParameterIgnored{
	   public String execute(ActionContext ac) {
	        HttpServletRequest request = ac.getServletRequest();
	        
	        String uri = request.getRequestURI();
	        String context = request.getContextPath();
	        
	        String path = uri.replaceFirst(context,"");
	        Pattern pattern = Pattern.compile("/([^/]+)(/(.*))?");
	        
	        Matcher match = pattern.matcher(path);
	        
	        if(match.find()){
	        	request.setAttribute("token",match.group(1));
	        	request.setAttribute("ver",match.group(3));
	        }
	        
	        return "success";
	   }
	   
}
