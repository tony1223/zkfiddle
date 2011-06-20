package org.zkoss.fiddle.files;


public interface IResourceFile {
	public String getPath();
	public byte[] getContentBytes();
	
	public void setContentBytes(byte[] cont);
}
