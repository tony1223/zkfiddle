package org.zkoss.fiddle.visualmodel;

public class CaseRequest {

	public enum Type{
		Direct,
		View,
		Sample
	}
	private FiddleSandbox sandbox;

	private String token;

	private String ver;

	private String zkversion;
	
	private Type type;
	
	
	public boolean needInitSandbox(){
		return getType() == Type.Direct || getType() == Type.View;
	}
	
	public CaseRequest(Type type){
		this.type = type;
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
