package org.zkoss.fiddle.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

	public static String read(String path) throws IOException{
		return read(new File(path), "UTF-8");
	}
	
	public static String read(File f) throws IOException{
		return read(f, "UTF-8");
	}

	public static String read(File f, String charset) throws IOException {

		StringBuffer content = new StringBuffer();

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));

		String input = br.readLine();
		while (input != null) {
			content.append(input);
			input = br.readLine();
		}

		br.close();

		return content.toString();
	}
}
