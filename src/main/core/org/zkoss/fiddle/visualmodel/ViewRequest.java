package org.zkoss.fiddle.visualmodel;

public class ViewRequest {

	private FiddleSandbox inst;

	private String token;

	private String ver;

	private String zkversion;

	public FiddleSandbox getFiddleInstance() {
		return inst;
	}

	public void setFiddleInstance(FiddleSandbox inst) {
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
}
