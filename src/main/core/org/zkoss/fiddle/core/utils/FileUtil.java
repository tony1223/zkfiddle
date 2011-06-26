package org.zkoss.fiddle.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

	public static String readIfExist(String path){
		try{
			return read(new File(path), "UTF-8");
		}catch(Exception e){
			return "";
		}
	}
	
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
			content.append(input+"\n");
			input = br.readLine();
		}

		br.close();

		return content.toString();
	}
}
