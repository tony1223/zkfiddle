package org.zkoss.fiddle.files;

import java.io.File;
import java.util.regex.Pattern;

import org.zkoss.fiddle.model.Resource;

public class ResourceFile implements IResourceFile {

	private static final String DOWNLOAD_JAVA_PACKAGE = "fiddle";

	private Resource resource;

	private static String replacedSeparator = String.valueOf(File.separatorChar);
	{
		if ("\\".equals(replacedSeparator)) {
			replacedSeparator = "\\\\";
		}
	}

	public ResourceFile(Resource ir) {
		if (ir == null)
			throw new IllegalArgumentException("resource can't be null");
		resource = ir;
	}

	private String convertPackageToFolder(String inp) {
		if (inp == null)
			return "";
		return inp.replaceAll(Pattern.quote("."), replacedSeparator);
	}

	public String getPath() {
		if (resource.getType() != Resource.TYPE_JAVA) {
			return "WebContent/" + resource.getName();
		} else {
			return "src/" + convertPackageToFolder(DOWNLOAD_JAVA_PACKAGE + resource.getPkg()) + "/"
					+ resource.getName();
		}
	}

	public String getContent() {
		return  resource.buildFinalConetnt(DOWNLOAD_JAVA_PACKAGE);
	}

	public byte[] getContentBytes() {
		return resource.buildFinalConetnt(DOWNLOAD_JAVA_PACKAGE).getBytes();
	}

	public void setContentBytes(byte[] cont) {
		throw new UnsupportedOperationException("unsupported");
	}

}
