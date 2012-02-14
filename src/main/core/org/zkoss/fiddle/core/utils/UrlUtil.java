package org.zkoss.fiddle.core.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;

import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONs;
import org.zkoss.json.parser.JSONParser;


public class UrlUtil {
	public static String getURLFriendlyTitle(String title) {
		if (title == null || "".equals(title.trim()))
			return "";

		StringBuffer sb = new StringBuffer();
		String[] tokens = title.split("[^a-zA-Z0-9]+");
		for (String str : tokens) {
			if(!"".equals(str))
				sb.append("-" + str);
		}
		return sb.toString();
	}
	@SuppressWarnings("unchecked")
	public static <T> T fetchJSON(URL u) throws ConnectException{
		String content  = fetchContent(u);
		JSONParser jsonp = new JSONParser();
		return (T) jsonp.parse(content);
	}
	
	public static String fetchContent(URL u) throws ConnectException{

		StringBuffer content = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(u.openConnection().getInputStream()));

			String input = br.readLine();
			while (input != null) {
				content.append(input);
				input = br.readLine();
			}

			br.close();
		}catch(FileNotFoundException e){
			throw new ConnectException("host reject conenction");
		}catch(ConnectException e){
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return content.toString();
	}
}
