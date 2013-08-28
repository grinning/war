package com.tommytony.war.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class HttpUtility {

	public static String get(String url) throws IOException {
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		if(con.getResponseCode() != 200) {
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuilder string = new StringBuilder();
		String s;
		while((s = in.readLine()) != null) {
			string.append(s);
		}
		in.close();
		con.disconnect();
		return string.toString();
	}
	
	public static String post(String url, String[] k, String[] v) throws Exception {
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setAllowUserInteraction(false);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		OutputStream out = con.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		for(int i = 0; i < k.length; i++) {
			writer.write(k[i]);
			writer.write("=");
			writer.write(new String(Charset.forName("UTF-8").encode(v[i]).array()));
			writer.write("&");
		}
		writer.close();
		out.close();
		
		if(con.getResponseCode() != 200) {
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuilder string = new StringBuilder();
		String s;
		while((s = in.readLine()) != null) {
			string.append(s);
		}
		in.close();
		con.disconnect();
		return string.toString();
	}
}
