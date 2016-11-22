package com.challenger.securitysteward.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

public class ImageUploader extends Thread {
	
	private String host;
	private List<Pair<String, String>> paras;
	private List<Pair<String, Bitmap>> images;
	private String path;
	private OnFinishedListener mListener = null;

	public ImageUploader(String host, List<Pair<String, String>> paras, List<Pair<String, Bitmap>> images, Context context) {
		this.host = host;
		this.paras = paras;
		this.images = images;
		this.path = context.getCacheDir().getPath();
	}

	@Override
	public void run() {
		
		String PREFIX = "--";
		String END_LINE = "\r\n";
		String BOUNDARY = UUID.randomUUID().toString();
		
		try {
			URL url = new URL(host);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");//设置请求的方式
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");  
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "utf-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			
			for(Pair<String, String> pi : paras) {
				if(pi.second == null) {
					continue;
				}
				// write para
				out.writeBytes(PREFIX + BOUNDARY + END_LINE);
				out.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"" + END_LINE,pi.first));
				out.writeBytes(END_LINE);
				String str = URLEncoder.encode(pi.second, "UTF-8");
				out.writeBytes(str + END_LINE);			
			}
			
			for(Pair<String, Bitmap> pi : images) {
				
				if(pi.second == null) {
					Log.d("DEBUG","key " + pi.first + " is null");
					continue;
				}
				String tmpname = "/" + String.valueOf(System.currentTimeMillis()) + ".png";
				OutputStream outStream;
				try {
					outStream = new FileOutputStream(new File(path + tmpname));
					Bitmap bitmap = pi.second;
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
					outStream.flush();
					outStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// write image
				out.writeBytes(PREFIX + BOUNDARY + END_LINE);
				out.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"%s", pi.first, tmpname, END_LINE));
				out.writeBytes("Content-Type: image/png" + END_LINE);
				out.writeBytes(END_LINE);
				
				//write image binary
				FileInputStream fileInput = new FileInputStream(new File(path + tmpname));
				byte[] bs = new byte[1024 * 4];
				int len;
				while((len = fileInput.read(bs)) != -1) {
					out.write(bs, 0, len);
				}
				// binary end
				
				out.writeBytes(END_LINE);
				if(fileInput != null) {
					fileInput.close();
				}
				File file = new File(path + tmpname);
				boolean res = file.delete();
				
				Log.d("UPLOADER", "result:" + res);
			}

			// write end
			out.writeBytes(PREFIX + BOUNDARY + PREFIX + END_LINE);
			
			Log.d("UPLOADER", "data writed");
			out.flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer strBuffer = new StringBuffer();
			String str;
			while((str = reader.readLine()) != null) {
				strBuffer.append(str);
			}
			
			System.out.println("response:" + strBuffer.toString());
			
			if(out != null ) {
				out.close();
			}
			if(reader != null) {
				reader.close();
			}
			if(mListener != null) {
				mListener.onFinish(strBuffer.toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setOnFinishedListener(OnFinishedListener listener) {
		mListener = listener;
	}
}
