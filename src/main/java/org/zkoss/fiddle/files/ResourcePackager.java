package org.zkoss.fiddle.files;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackager {

	private List<IResourceFile> list;

	public ResourcePackager() {
		list = new ArrayList<IResourceFile>();
	}

	public static ResourcePackager list() {
		return new ResourcePackager();
	}

	public ResourcePackager add(List<IResourceFile> rf) {
		list.addAll(rf);
		return this;
	}
	
	public ResourcePackager add(ResourceFile rf) {
		list.add(rf);
		return this;
	}

	public void export(OutputStream os) throws IOException {
		ZipOutputStream out = new ZipOutputStream(os);
		applyResourceFiles(out);
		out.close();
	}

	private void applyResourceFiles(ZipOutputStream out) throws IOException {
		for (IResourceFile rf : list) {
			out.putNextEntry(new ZipEntry(rf.getPath()));
			out.write(rf.getContentBytes());
		}
	}

//	public static void main(String[] args) throws IOException {
//		FileOutputStream fos = new FileOutputStream(new File("test.zip"));
//
//		IResource java1 = ResourceFactory.getDefaultResource(IResource.TYPE_JAVA);
//		IResource java2 = java1.clone();
//		java2.setName("test.java");
//		IResource java3 = java1.clone();
//		java3.setName("test3.java");
//
//		ResourcePackager.list()
//				.add(new ResourceFile(java1))
//				.add(new ResourceFile(java2))
//				.add(new ResourceFile(java3))
//				.add(new ResourceFile(ResourceFactory.getDefaultResource(IResource.TYPE_HTML)))
//				.add(new ResourceFile(ResourceFactory.getDefaultResource(IResource.TYPE_CSS)))
//				.add(new ResourceFile(ResourceFactory.getDefaultResource(IResource.TYPE_JS)))
//				.add(new ResourceFile(ResourceFactory.getDefaultResource(IResource.TYPE_ZUL))).export(fos);
//
//		fos.close();
//	}
}
