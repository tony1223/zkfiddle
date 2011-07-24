package org.zkoss.fiddle.visualmodel;

public class ViewRequest {

	public enum Type{
		Direct,
		View,
		Sample
	}
	private FiddleSandbox inst;

	private String token;

	private String ver;

	private String zkversion;
	
	private Type type;
	
	
	public boolean needInitSandbox(){
		return getType() == Type.Direct || getType() == Type.View;
	}
	
	public ViewRequest(Type type){
		this.type = type;
	}
	
	
	public String getFiddleDirectURL(){
		return inst.getSrc(getToken(), Integer.parseInt(getTokenVersion()));
	}

	public FiddleSandbox getFiddleInstance() {
		return inst;
	}

	public void setFiddleSandbox(FiddleSandbox inst) {
		this.inst = inst;
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
