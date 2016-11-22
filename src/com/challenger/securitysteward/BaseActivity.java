package com.challenger.securitysteward;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.WindowManager;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.utils.Utils;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiGroup;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.api.GizWifiSSID;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;

/**
 * 
 * @author Shannon
 * 
 * This class extends Activity to process translucent status bar and throw Giz SDK listener.
 *
 */

public abstract class BaseActivity extends Activity {
	
	protected TopBar mTopBar;
	protected Bundle mBundle;
	protected String mValueLeftText;
	protected String mCurUser = null;
	protected String mCurPwd = null;
	
	private GizWifiSDKListener mGizSDKListener = new GizWifiSDKListener() {
		
		@Override
		public void didBindDevice(GizWifiErrorCode result, String did) {
			BaseActivity.this.didBindDevice(result, did);
		}

		@Override
		public void didChangeUserInfo(GizWifiErrorCode result) {
			BaseActivity.this.didChangeUserInfo(result);
		}

		@Override
		public void didChangeUserPassword(GizWifiErrorCode result) {
			BaseActivity.this.didChangeUserPassword(result);
		}

		@Override
		public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
			BaseActivity.this.didDiscovered(result, deviceList);
		}

		@Override
		public void didGetUserInfo(GizWifiErrorCode result, GizUserInfo userInfo) {
			BaseActivity.this.didGetUserInfo(result, userInfo);
		}

		@Override
		public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
			BaseActivity.this.didNotifyEvent(eventType, eventSource, eventID, eventMessage);
		}

		@Override
		public void didRegisterUser(GizWifiErrorCode result, String uid, String token) {
			BaseActivity.this.didRegisterUser(result, uid, token);
		}

		@Override
		public void didRequestSendPhoneSMSCode(GizWifiErrorCode result, String token) {
			BaseActivity.this.didRequestSendPhoneSMSCode(result, token);
		}

		@Override
		public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
			BaseActivity.this.didUserLogin(result, uid, token);
		}

		@Override
		public void didVerifyPhoneSMSCode(GizWifiErrorCode result) {
			BaseActivity.this.didVerifyPhoneSMSCode(result);
		}

		@Override
		public void didChannelIDBind(GizWifiErrorCode result) {
			BaseActivity.this.didChannelIDBind(result);
		}

		@Override
		public void didChannelIDUnBind(GizWifiErrorCode result) {
			BaseActivity.this.didChannelIDUnBind(result);
		}

		@Override
		public void didDisableLAN(GizWifiErrorCode result) {
			BaseActivity.this.didDisableLAN(result);
		}

		@Override
		public void didGetCaptchaCode(GizWifiErrorCode result, String token, String captchaId, String captchaURL) {
			BaseActivity.this.didGetCaptchaCode(result, token, captchaId, captchaURL);
		}

		@Override
		public void didGetCurrentCloudService(GizWifiErrorCode result,
				ConcurrentHashMap<String, String> cloudServiceInfo) {
			BaseActivity.this.didGetCurrentCloudService(result, cloudServiceInfo);
		}

		@Override
		public void didGetGroups(GizWifiErrorCode result, List<GizWifiGroup> groupList) {
			BaseActivity.this.didGetGroups(result, groupList);
		}

		@Override
		public void didGetSSIDList(GizWifiErrorCode result, List<GizWifiSSID> ssidInfoList) {
			BaseActivity.this.didGetSSIDList(result, ssidInfoList);
		}

		@Override
		public void didSetDeviceOnboarding(GizWifiErrorCode result, String mac, String did, String productKey) {
			BaseActivity.this.didSetDeviceOnboarding(result, mac, did, productKey);
		}

		@Override
		public void didUnbindDevice(GizWifiErrorCode result, String did) {
			BaseActivity.this.didUnbindDevice(result, did);
		}
		
	};
	
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		GizWifiSDK.sharedInstance().setListener(mGizSDKListener);
	}
	
	protected void saveUserInfo() {
		saveObject(Utils.KEY_SHARED_USER_INFO, Utils.KEY_SHARED_USER_BOJECT, Utils.getUserManagement());
	}
	
	protected void didBindDevice(GizWifiErrorCode result, String did) {
	}

	protected void didChangeUserInfo(GizWifiErrorCode result) {
		
	}

	protected void didChangeUserPassword(GizWifiErrorCode result) {
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			Utils.getUserManagement().logout(false);
			saveUserInfo();
		}
	}

	protected void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
	}

	protected void didGetUserInfo(GizWifiErrorCode result, GizUserInfo userInfo) {
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			Utils.getUserManagement().setUserInfo(userInfo);
			saveUserInfo();
		} else {
			Utils.setToastBottom(getBaseContext(), R.string.toast_error_user_info_fail);
		}
	}

	protected void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
	}

	protected void didRegisterUser(GizWifiErrorCode result, String uid, String token) {
	}

	protected void didRequestSendPhoneSMSCode(GizWifiErrorCode result, String token) {
	}

	protected void didUserLogin(GizWifiErrorCode result, String uid, String token) {
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			// Success, Get uid and token
			String olduid = Utils.getUserManagement().getUid();
			if(olduid != null && !olduid.equals(uid))
				Utils.getUserManagement().logout(true);
			Utils.getUserManagement().login(mCurUser,mCurPwd, uid, token);
			saveUserInfo();
		}
	}

	protected void didVerifyPhoneSMSCode(GizWifiErrorCode result) {
	}
	
	protected void didChannelIDBind(GizWifiErrorCode result) {
	}

	protected void didChannelIDUnBind(GizWifiErrorCode result) {
	}

	protected void didDisableLAN(GizWifiErrorCode result) {
	}

	protected void didGetCaptchaCode(GizWifiErrorCode result, String token, String captchaId, String captchaURL) {
	}

	protected void didGetCurrentCloudService(GizWifiErrorCode result,
			ConcurrentHashMap<String, String> cloudServiceInfo) {
	}

	protected void didGetGroups(GizWifiErrorCode result, List<GizWifiGroup> groupList) {
	}

	protected void didGetSSIDList(GizWifiErrorCode result, List<GizWifiSSID> ssidInfoList) {
	}

	protected void didSetDeviceOnboarding(GizWifiErrorCode result, String mac, String did, String productKey) {
	}

	protected void didUnbindDevice(GizWifiErrorCode result, String did) {
	}
	
	@Override
	public void onBackPressed() {
		
		if(mTopBar != null && mTopBar.hasLeftButton()) {
			mTopBar.performLeftClick();
			return;
		}
		super.onBackPressed();
		setResult(RESULT_CANCELED);
	}
	
	public Object readObject(String shared, String key) {
		
		SharedPreferences preferences = getSharedPreferences(shared,
				MODE_PRIVATE);
		String productBase64 = preferences.getString(key, null);
		if (productBase64 == null) {
				
			return null;
		}
		
		byte[] base64 = Base64.decodeBase64(productBase64.getBytes());
		
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				Object ret = bis.readObject();
				return ret;
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		return null;
	}

	public void saveObject(String shared, String key, Object value) {
		
		SharedPreferences preferences = getSharedPreferences(shared,
				MODE_PRIVATE);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {

			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(value);

			String productBase64 = new String(Base64.encodeBase64(baos
					.toByteArray()));

			Editor editor = preferences.edit();
			editor.putString(key, productBase64);
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getUrlPara(String url, String key) {
		
		try {
			
			String str = url.substring(url.indexOf("?") + 1);
			int sta = str.indexOf(key) + key.length() + 1;
			int end = sta;
			while(end < str.length() && str.charAt(end) != '&') {
				end++;
			}
			return str.substring(sta, end);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date stringToDate(String date) {
		
		try {
			String[] str = date.split("-");
			int year = Integer.parseInt(str[0]);
			int month = Integer.parseInt(str[1]);
			int day = Integer.parseInt(str[2]);
			@SuppressWarnings("deprecation")
			Date ret = new Date(year - 1900, month - 1, day);
			return ret;			
		} catch(Exception e) {
			
		}
		return new Date();
	}
	protected abstract void initView();
	protected abstract void initEventListener();
}
