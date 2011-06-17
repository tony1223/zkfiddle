package org.zkoss.fiddle.files;

import java.io.File;
import java.util.regex.Pattern;

import org.zkoss.fiddle.model.api.IResource;

public class ResourceFile implements IResourceFile {

	private IResource resource;

	public ResourceFile(IResource ir) {
		if (ir == null)
			throw new IllegalArgumentException("resource can't be null");
		resource = ir;
	}

	public String getPath() {
		if (resource.getType() != IResource.TYPE_JAVA) {
			return resource.getName();
		} else {
			String fullpackage = resource.getFullPackage().replaceAll(Pattern.quote("."),
					Pattern.quote("" + File.separatorChar));
			return "src/" + fullpackage + "/" + resource.getName();
		}

	}

	public String getContent() {
		return resource.getContent();
	}

}
