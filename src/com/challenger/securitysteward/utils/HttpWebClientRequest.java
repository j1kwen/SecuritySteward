package com.challenger.securitysteward.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import android.util.Log;
import android.util.Pair;

public class HttpWebClientRequest extends Thread {
	
	private String host;
	private List<Pair<String, String>> para;
	private OnReceiveBytes mListener = null;
	
	public HttpWebClientRequest(String host, List<Pair<String, String>> para) {
		this.host = host;
		this.para = para;
	}
	
	public interface OnReceiveBytes {
		public void onReceiveBytes(byte[] bytes);
	}
	
	@Override
	public void run() {
		HttpURLConnection conn = null;
		URL url = null;
		byte[] bytes = null;
		try {
			
			if(host.indexOf("http://") == -1) {
				StringBuilder str = new StringBuilder("http://");
				str.append(host);
				host = str.toString();
			}
			url = new URL(host);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setReadTimeout(3000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");  
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();

			//------------字符流写入数据------------
			OutputStream out = conn.getOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			bw.write(getParaString(para));
			Log.d("PARA", String.format("para => %s", getParaString(para)));
			bw.flush();
			out.close();
			bw.close();
			
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){		
				InputStream in = conn.getInputStream();
				ByteArrayOutputStream ops = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len;
				while((len = in.read(buffer)) != -1) {
					ops.write(buffer, 0, len);
				}
				bytes = ops.toByteArray();
				in.close();
				ops.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			conn.disconnect();
		}
		if(mListener != null) {
			mListener.onReceiveBytes(bytes);
		}
	}
	
	public void setOnFinishedListener(OnReceiveBytes listener) {
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
