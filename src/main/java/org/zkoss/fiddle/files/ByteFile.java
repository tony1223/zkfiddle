package org.zkoss.fiddle.files;


public class ByteFile implements IResourceFile {


	private String path;
	private byte[] content;
	
	public ByteFile(String _path,byte[] cont) {
		this.path = _path;
		this.content = cont;
	}

	public String getPath() {
		return path;
	}

	public byte[] getContentBytes() {
		return content;
	}

}
