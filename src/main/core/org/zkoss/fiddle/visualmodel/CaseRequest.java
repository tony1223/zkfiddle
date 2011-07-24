package org.zkoss.fiddle.visualmodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaseRequest {

	private static Pattern view = Pattern.compile("^/([^/.]{5,}?)/(\\d+)(/v([0-9\\.]+)/?)?.*$");
	
	public enum Type{
		Direct("/direct/",true),
		View("/view/",true),
		Sample("/sample/",true);
//		,Widget("/widget/",true);
		
		private String prefix ;
		private boolean needInitSandbox;
		private Type(String pathStart,boolean needInitSandbox){
			this.prefix = pathStart; 
			this.needInitSandbox = needInitSandbox;
		}
		
		public boolean isNeedInitSandbox() {
			return needInitSandbox;
		}
		
		public boolean is(String path){
			return (path != null && path.startsWith(prefix));
		}
		
		public String getSubpath(String oldpath){
			return oldpath.substring(prefix.length() -1);
		}
		
		public String getPrefix() {
			return prefix;
		}
		
		public String getPrefixNotStartWithSlash() {
			return prefix.substring(1);
		}
		
	}
	private FiddleSandbox sandbox;

	private String token;

	private String ver;

	private String zkversion;
	
	private Type type;
	
	public CaseRequest(Type type){
		this.type = type;
	}


	public static CaseRequest getCaseRequest(String path){
		for(Type type:Type.values()){
			if(type.is(path)){
				return getRequestCase(type,path);
			}
		}
		return null;
			
	}
	
	private static CaseRequest getRequestCase(Type type,String path){
		Matcher match = view.matcher(type.getSubpath(path));
		if (match.find()) {
			String version = match.group(2);
			String token = match.group(1);
			String zkversion = match.group(4);

			CaseRequest viewRequest = new CaseRequest(type);
			viewRequest.setToken(token);
			viewRequest.setTokenVersion(version);
			viewRequest.setZkversion(zkversion);
			return viewRequest;
		}
		return null;
	}
	
	public boolean needInitSandbox(){
		return getType() == Type.Direct || getType() == Type.View;
	}
	
	public String getFiddleDirectURL(){
		return sandbox.getSrc(getToken(), Integer.parseInt(getTokenVersion()));
	}

	public FiddleSandbox getFiddleSandbox() {
		return sandbox;
	}

	public void setFiddleSandbox(FiddleSandbox inst) {
		this.sandbox = inst;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenVersion() {
		return ver;
	}

	public void setTokenVersion(String ver) {
		this.ver = ver;
	}

	public String getZkversion() {
		return zkversion;
	}

	public void setZkversion(String zkversion) {
		this.zkversion = zkversion;
	}

	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	
}
