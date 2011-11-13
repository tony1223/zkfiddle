package org.zkoss.fiddle.visualmodel;

import java.util.List;

public class FiddleSandboxGroup {

	private String name;

	private List<FiddleSandbox> sandboxs;

	public FiddleSandboxGroup(String name, List<FiddleSandbox> sandboxs) {
		super();
		this.name = name;
		this.sandboxs = sandboxs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FiddleSandbox> getSandboxs() {
		return sandboxs;
	}

	public void setSandboxs(List<FiddleSandbox> sandboxs) {
		this.sandboxs = sandboxs;
	}
}
