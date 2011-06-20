package org.zkoss.fiddle.files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtil {

	public static List<IResourceFile> getFiles(File f) throws IOException {
		return getFiles(f, "");
	}

	public static List<IResourceFile> getFiles(File f, String basePath) throws IOException {
		ZipFile zf = new ZipFile(f);
		Enumeration entries = zf.entries();
		ArrayList<IResourceFile> ret = new ArrayList<IResourceFile>();

		while (entries.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) entries.nextElement();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			InputStream is = zf.getInputStream(ze);
			int c = is.read();
			while (c != -1) {
				bos.write(c);
				c = is.read();
			}
			is.close();
			bos.close();
			ret.add(new ByteFile(ze.getName(), bos.toByteArray()));
		}

		return ret;
	}

}
