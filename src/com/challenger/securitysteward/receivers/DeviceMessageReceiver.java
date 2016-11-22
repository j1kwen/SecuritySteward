package com.challenger.securitysteward.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceMessageReceiver extends BroadcastReceiver {

	private final String TAG = "MessageReceiver";
	private MessageListener mListener;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(mListener != null) {
			Log.d(TAG, "will send message across interface");
			String data = intent.getExtras().getString("data");
			mListener.onReceive(data);
		}
	}
	
	public interface MessageListener {
		public void onReceive(String message);
	}
	
	public void setMessageListener(MessageListener listener) {
		this.mListener = listener;
	}
}
