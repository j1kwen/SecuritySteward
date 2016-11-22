package com.challenger.securitysteward.services;

import com.igexin.sdk.PushManager;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends BaseService {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("DEBUG","Main Service Starting...ok");
		PushManager.getInstance().initialize(this);
		Log.i("DEBUG","Push Service initializing...ok");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
