package com.challenger.securitysteward.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import android.util.Log;
import android.util.Pair;

public class HttpWebRequest extends Thread {
	
	private String host;
	private List<Pair<String, String>> para;
	private OnFinishedListener mListener = null;
	
	public HttpWebRequest(String host, List<Pair<String, String>> para) {
		this.host = host;
		this.para = para;
	}
	
	@Override
	public void run() {
		HttpURLConnection conn = null;
		URL url = null;
		String ret = "";
		try {
			
			if(!host.startsWith("http://") && !host.startsWith("https://")) {
				StringBuilder str = new StringBuilder("http://");
				str.append(host);
				host = str.toString();
			}
			url = new URL(host);
			conn = (HttpURLConnection) url.openConnection();
			if(para != null) {				
				conn.setConnectTimeout(3000);
				conn.setUseCaches(false);
				conn.setInstanceFollowRedirects(true);
				conn.setReadTimeout(3000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");  
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
				conn.setRequestProperty("Connection", "Keep-Alive");
			} else {
				conn.setRequestMethod("GET");
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			}
			conn.connect();

			if(para != null) {				
				//------------字符流写入数据------------
				OutputStream out = conn.getOutputStream();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
				bw.write(getParaString(para));
				Log.d("PARA", String.format("para => %s", getParaString(para)));
				bw.flush();
				out.close();
				bw.close();
			}
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				//------------字符流读取服务端返回的数据------------					
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String str = null;
				StringBuffer buffer = new StringBuffer();
				while((str = br.readLine())!=null){
					buffer.append(str);
				}
				in.close();
				br.close();
				ret =  buffer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			conn.disconnect();
		}
		if(mListener != null) {
			mListener.onFinish(ret);
		}
	}
	
	public void setOnFinishedListener(OnFinishedListener listener) {
		mListener = listener;
	}
	
	private String getParaString(List<Pair<String, String>> paras) {
		StringBuilder str = new StringBuilder();
		for(Pair<String, String> pi : paras) {
			String val = "";
			try {
				val = URLEncoder.encode(pi.second, "UTF-8");
				str.append(String.format("%s=%s&", pi.first, val));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String ret = str.toString();
		String par = ret.substring(0, ret.length() - 1);
		return par;
	}
}
