package org.zkoss.fiddle.exception;

public class SandboxNotFoundException extends Exception {

	public enum Type {
		HASH, ZK_VERSION,DEFAULT
	}

	private Type type;

	public SandboxNotFoundException(Type in) {
		this.type = in;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
