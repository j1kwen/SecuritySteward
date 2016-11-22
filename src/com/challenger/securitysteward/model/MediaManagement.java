package com.challenger.securitysteward.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.utils.SystemUtils;

public class MediaManagement {
	
	private static MediaManagement single;
	private Context context;
	private Map<String, Bitmap> mListBitmap;
	private Map<String, String> mListMD5;
	
	public interface OnReloadImage {
		public void onReload();
	}
	
	public static synchronized MediaManagement getInstance() {
		if(single == null) {
			single = new MediaManagement();
		}
		return single;
	}
	
	public void initialize(Context context) {
		this.context = context;
		mListBitmap = new HashMap<String, Bitmap>();
		mListMD5 = new HashMap<String, String>();
		loadImageFile();
	}
	
	private Bitmap bytesToBitmap(byte[] bytes) {
		Bitmap bitmap = null;
		if(bytes == null) {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		} else {
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		return bitmap;
	}
	
	public void playReceived() {
		MediaPlayer mediaPlayer;
		mediaPlayer = MediaPlayer.create(this.context, R.raw.push_received);
		mediaPlayer.start();
	}
	
	public void playSystemMessage() {
		MediaPlayer mediaPlayer;
		mediaPlayer = MediaPlayer.create(this.context, R.raw.water);
		mediaPlayer.start();
	}
	
	public void playScanBeep() {
		MediaPlayer mediaPlayer;
		mediaPlayer = MediaPlayer.create(this.context, R.raw.beep);
		mediaPlayer.start();
	}
	
	public Bitmap getDeviceBitmap(String did) {
		Bitmap bitmap = mListBitmap.get(did);
		if(bitmap == null) {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		}
		return bitmap;
	}
	
	public String getImageMd5(String did) {
		String ret = mListMD5.get(did);
		return ret == null ? "" : ret;
	}
	
	public byte[] getImageBytes(String did) {
		String path = context.getCacheDir().toString() + "/" + did + ".png";
		File file = new File(path);
		if(!file.exists()) {
			return null;
		}
		try {
			FileInputStream fs = new FileInputStream(file);
			ByteArrayOutputStream bys = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while((len = fs.read(buffer)) != -1) {
				bys.write(buffer, 0, len);
			}
			fs.close();
			bys.close();
			Log.d("GET IMAGE","did " + did + " image get success");
			return bys.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void writeImageToLocal(String did, byte[] bytes) {
		try {
			if(bytes == null || bytes.length <= 1) {
				return;
			}
			String path = context.getCacheDir().toString() + "/" + did + ".png";
			File file = new File(path);
			FileOutputStream ops = new FileOutputStream(file);
			ByteArrayInputStream ips = new ByteArrayInputStream(bytes);
			byte[] buffer = new byte[1024];
			int len;
			while((len = ips.read(buffer)) != -1) {
				ops.write(buffer, 0, len);
			}
			ips.close();
			ops.flush();
			ops.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteImageLocal(String did) {
		String path = context.getCacheDir().toString() + "/" + did + ".png";
		File file = new File(path);
		if(file.exists()) {
			file.delete();
		}
	}
	
	static class MyHandler extends Handler {
		private OnReloadImage listener;
		public MyHandler(OnReloadImage listener) {
			this.listener = listener;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			listener.onReload();
		}
	}
	public void reloadLocalImage(OnReloadImage listener) {
		final MyHandler handler = new MyHandler(listener);
		Runnable runnable = new Runnable() {
			public void run() {
				loadImageFile();
				handler.sendMessage(new Message());
			}
		};
		new Thread(runnable).start();
	}
	private void loadImageFile() {
		mListBitmap.clear();
		mListMD5.clear();
		File file = context.getCacheDir();
		String[] fs = file.list();
		for(String f : fs) {
			if(f.endsWith(".png")) {
				String did = f.substring(0, f.length() - 4);
				byte[] bytes = getImageBytes(did);
				mListBitmap.put(did, bytesToBitmap(bytes));
				mListMD5.put(did, SystemUtils.getMD5String(bytes));
			}
		}
	}
	
	@SuppressLint("SdCardPath")
	public void exportImage() {
	   String path = "/sdcard/";
   	try {
   		String file = context.getCacheDir().toString() + "/123456.png";
   		FileInputStream ips = new FileInputStream(new File(file));
		byte[] buffer = new byte[1024];
		int len;
		FileOutputStream ops = new FileOutputStream(new File(path + "123456.png"));
		while((len = ips.read(buffer)) != -1) {
			try {
				ops.write(buffer, 0, len);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ips.close();
		ops.close();
   	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
}
