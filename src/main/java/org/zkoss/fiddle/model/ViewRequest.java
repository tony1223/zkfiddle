package org.zkoss.fiddle.model;

public class ViewRequest {

	private FiddleInstance inst;

	private String token;

	private String ver;

	private String zkversion;

	public FiddleInstance getInstance() {
		return inst;
	}

	public void setInst(FiddleInstance inst) {
		this.inst = inst;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getZkversion() {
		return zkversion;
	}

	public void setZkversion(String zkversion) {
		this.zkversion = zkversion;
	}
}
