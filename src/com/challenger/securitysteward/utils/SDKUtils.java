package com.challenger.securitysteward.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.challenger.securitysteward.model.DeviceItemModel;
import com.challenger.securitysteward.model.DeviceMessage;
import com.challenger.securitysteward.utils.HttpWebClientRequest.OnReceiveBytes;

public class SDKUtils {

	private static SDKUtils single = null;
	private Context context = null;
	
	public static synchronized SDKUtils getInstance() {
		if(single == null) {
			single = new SDKUtils();
		}
		return single;
	}
	
	public void initialize(Context context) {
		this.context = context;
	}
	
	public void bindDevice(String secret, String cid, String did, String name, Bitmap image, OnReceivedResult listener) {
		String host = Utils.getBindServereUrl();
		List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
		para.add(new Pair<String, String>("did", did));
		para.add(new Pair<String, String>("cid", cid));
		para.add(new Pair<String, String>("secret", secret));
		para.add(new Pair<String, String>("name", name));
		
		List<Pair<String, Bitmap>> images = new ArrayList<Pair<String, Bitmap>>();
		images.add(new Pair<String, Bitmap>("image", image));
		
		final MyHandler handler = new MyHandler(listener);
		
		ImageUploader thread = new ImageUploader(host, para, images, context);
		thread.setOnFinishedListener(new OnFinishedListener() {
			
			@Override
			public void onFinish(String result) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("data", result);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		});
		
		thread.start();
	}
	
	public void unbindDevice(String secret, String cid, String did, OnReceivedResult listener) {
		String host = Utils.getUnbindServerUrl();
		List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
		para.add(new Pair<String, String>("secret", secret));
		para.add(new Pair<String, String>("cid", cid));
		para.add(new Pair<String, String>("did", did));
		
		final MyHandler handler = new MyHandler(listener);
		
		HttpWebRequest thread = new HttpWebRequest(host, para);
		thread.setOnFinishedListener(new OnFinishedListener() {
			
			@Override
			public void onFinish(String result) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("data", result);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		});
		thread.start();
	}
	
	public void getDevicesList(String secret, String cid, OnReceivedResult listener) {
		String host = Utils.getDeviceListServerUrl();
		List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
		para.add(new Pair<String, String>("cid", cid));
		para.add(new Pair<String, String>("secret", secret));
		
		final MyHandler handler = new MyHandler(listener);
		
		HttpWebRequest thread = new HttpWebRequest(host, para);
		thread.setOnFinishedListener(new OnFinishedListener() {
			
			@Override
			public void onFinish(String result) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("data", result);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		});
		thread.start();
	}
	
	public void setDeviceInfo(String secret, String cid, DeviceItemModel device, Bitmap image, OnReceivedResult listener) {
		String host = Utils.getModifyServerUrl();
		List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
		para.add(new Pair<String, String>("secret", secret));
		para.add(new Pair<String, String>("cid", cid));
		para.add(new Pair<String, String>("did", device.getDid()));
		para.add(new Pair<String, String>("name", device.getName()));
		para.add(new Pair<String, String>("level", String.valueOf(device.getLevel())));
		para.add(new Pair<String, String>("remark", device.getRemark()));
		
		List<Pair<String, Bitmap>> images = new ArrayList<Pair<String, Bitmap>>();
		images.add(new Pair<String, Bitmap>("image", image));
		
		final MyHandler handler = new MyHandler(listener);
		
		ImageUploader thread = new ImageUploader(host, para, images, context);
		thread.setOnFinishedListener(new OnFinishedListener() {
			
			@Override
			public void onFinish(String result) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("data", result);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		});
		
		thread.start();
	}
	
	public void deleteDeviceImage(String secret, String cid, String did) {
		String host = Utils.getImageServerUrl();
		List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
		para.add(new Pair<String, String>("secret", secret));
		para.add(new Pair<String, String>("cid", cid));
		para.add(new Pair<String, String>("did", did));
		para.add(new Pair<String, String>("del", "1"));
		HttpWebClientRequest thread = new HttpWebClientRequest(host, para);
		thread.start();
	}
	
	public void deleteDeviceMessage(String secret, String cid, List<DeviceMessage> msg) {
		String host = Utils.getDelMsgServerUrl();
		List<Pair<String, String>> para = new ArrayList<Pair<String,String>>();
		para.add(new Pair<String, String>("secret", secret));
		para.add(new Pair<String, String>("cid", cid));
		StringBuilder str = new StringBuilder();
		for(DeviceMessage dev : msg) {
			if(dev.isSystemMsg()) {
				continue;
			}
			String i = dev.getHref();
			int sta = i.indexOf("key=") + 4;
			str.append(i.substring(sta, sta + 32) + "|");
		}
		if(str.length() > 0) {
			String msgStr = str.toString();
			para.add(new Pair<String, String>("msg", msgStr.substring(0, msgStr.length() - 1)));
			HttpWebRequest thread = new HttpWebRequest(host, para);
			thread.start();
		}
	}
	
	public void deleteDeviceMessage(String secret, String cid, Map<String, List<DeviceMessage>> msg) {
		List<DeviceMessage> list = new ArrayList<DeviceMessage>();
		for(List<DeviceMessage> li : msg.values()) {
			list.addAll(li);
		}
		deleteDeviceMessage(secret, cid, list);
	}
	
	public void getDeviceImage(String secret, String cid, String did, OnReceivedBytes listener) {
		String host = Utils.getImageServerUrl();
		List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
		para.add(new Pair<String, String>("secret", secret));
		para.add(new Pair<String, String>("cid", cid));
		para.add(new Pair<String, String>("did", did));

		final HandlerBytes handler = new HandlerBytes(did, listener);
		
		HttpWebClientRequest thread = new HttpWebClientRequest(host, para);
		thread.setOnFinishedListener(new OnReceiveBytes() {
			
			@Override
			public void onReceiveBytes(byte[] bytes) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putByteArray("data", bytes);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		});
		
		thread.start();
	}
	
	public void getUpdateXmlFile(OnReceivedResult listener) {
		String host = "https://raw.githubusercontent.com/763461297/SecuritySteward/master/AndroidManifest.xml";
		final MyHandler handler = new MyHandler(listener);
		HttpWebRequest thread = new HttpWebRequest(host, null);
		thread.setOnFinishedListener(new OnFinishedListener() {
			
			@Override
			public void onFinish(String result) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("data", result);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		});
		
		thread.start();
	}
	
	static class MyHandler extends Handler {
		
		private OnReceivedResult listener;
		public MyHandler(OnReceivedResult listener) {
			this.listener = listener;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			listener.onReceived(msg.getData().getString("data"));
		}
	}
	
	static class HandlerBytes extends Handler {
		private OnReceivedBytes listener;
		private String did;
		public HandlerBytes(String did, OnReceivedBytes listener) {
			this.listener = listener;
			this.did = did;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			listener.onReceivedByte(did, msg.getData().getByteArray("data"));
		}
	}
	
	public interface OnReceivedResult {
		public void onReceived(String result);
	}
	
	public interface OnReceivedBytes {
		public void onReceivedByte(String did, byte[] bytes);
	}
}
