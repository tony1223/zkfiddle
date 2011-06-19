package org.zkoss.fiddle.files;

import java.io.File;
import java.util.regex.Pattern;

import org.zkoss.fiddle.model.api.IResource;

public class ResourceFile implements IResourceFile {

	private IResource resource;

	private static String replacedSeparator = String.valueOf(File.separatorChar);
	{
		if("\\".equals(replacedSeparator)){
			replacedSeparator = "\\\\";
		}
	}
	
	public ResourceFile(IResource ir) {
		if (ir == null)
			throw new IllegalArgumentException("resource can't be null");
		resource = ir;
	}

	private String convertPackageToFolder(String inp){
		if(inp == null) return "";
		return inp.replaceAll(Pattern.quote("."),replacedSeparator);
	}
	
	public String getPath() {
		if (resource.getType() != IResource.TYPE_JAVA) {
			return "WebContent/" + resource.getName();
		} else {
			return "src/" + convertPackageToFolder(resource.getFullPackage()) + "/" + resource.getName();
		}
	}

	public String getContent() {
		return resource.getContent();
	}
	
	public byte[] getContentBytes() {
		return resource.getContent().getBytes();
	}

}
