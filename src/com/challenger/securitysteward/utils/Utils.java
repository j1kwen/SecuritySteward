package com.challenger.securitysteward.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.model.DeviceManagement;
import com.challenger.securitysteward.model.MessageManagement;
import com.challenger.securitysteward.model.MediaManagement;
import com.challenger.securitysteward.model.UserManagement;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizLogPrintLevel;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.igexin.sdk.PushManager;

public class Utils {
	
	// Server
	private static final String HOST				= "http://api.dogest.cn/android";
	//private static final String HOST				= "http://192.168.1.123:8080/android";
	private static final String BIND				= "/bind";
	private static final String DEVICE_LIST			= "/data";
	private static final String UNBIND				= "/unbind";
	private static final String UPLOAD				= "/upload";
	private static final String MODIFY				= "/modify";
	private static final String IMAGE				= "/image";
	
	public static String getImageServerUrl() {
		return new String(HOST + IMAGE);
	}
	
	public static String getModifyServerUrl() {
		return new String(HOST + MODIFY);
	}
	
	public static String getUploadServerUrl() {
		return new String(HOST + UPLOAD);
	}
	
	public static String getBindServereUrl() {
		return new String(HOST + BIND);
	}
	
	public static String getDeviceListServerUrl() {
		return new String(HOST + DEVICE_LIST);
	}
	
	public static String getUnbindServerUrl() {
		return new String(HOST + UNBIND);
	}
	
	// SF integer
	public static final int RESULT_USER_LOGIN_SUCCESS		= 0x00000001;
	public static final int RESULT_USER_REGISTER_SUCCESS	= 0x00000002;
	public static final int RESULT_MODIFY_PWD_SUCCESS		= 0x00000004;
	public static final int REQUEST_FROM_USER_VIEW			= 0x00000010;
	public static final int REQUEST_FROM_USER_LOGIN			= 0x00000020;
	public static final int REQUEST_FROM_VERIFY_PHONE		= 0x00000040;
	public static final int REQUEST_FROM_FORGET				= 0X00000080;
	public static final int REQUEST_CODE_SCAN 				= 0x00000100;
	public static final int REQUEST_MODIFY_ITEM				= 0x00000200;

	// QR String
	public static final String DECODED_CONTENT_KEY			= "codedContent";
	public static final String DECODED_BITMAP_KEY			= "codedBitmap";
	
	//SF string
	public static final String KEY_ITEM_TITLE				= "item_title";
	public static final String KEY_ITEM_MAX_LEN				= "item_max_len";
	public static final String KEY_ITEM_STRING				= "item_string";
	public static final String KEY_BACK_TEXT				= "btn_left_text";
	public static final String KEY_RIGHT_BTN_TEXT			= "btn_right_text";
	public static final String KEY_CURRENT_USER_UID			= "cur_user_uid";
	public static final String KEY_CURRENT_USER_TOKEN		= "cur_user_token";
	public static final String KEY_USER_PHONE_NUMBER		= "cur_user_phone";
	public static final String KEY_ACTION_TO				= "verify_from";
	public static final String KEY_SHARED_USER_INFO			= "share_user_info";
	public static final String KEY_SHARED_USER_NAME			= "share_user_name";
	public static final String KEY_SHARED_USER_PWD			= "share_user_pwd";
	public static final String KEY_SHARED_USER_BOJECT		= "share_user_obj";
	public static final String KEY_QRCODE_DID				= "did";
	public static final String KEY_QRCODE_STRING			= "qrcode_string";
	public static final String KEY_MESSAGE					= "message";
	public static final String KEY_MESSAGE_BODY				= "message_body";
	public static final String EXTRA_BUNDLE					= "extra_bund";
	
	// SF value
	public static final String VALUE_TO_REGISTER			= "to_reg";
	public static final String VALUE_TO_FORGET				= "to_forget";
	
	// APP development constant
	public static final String APP_ID						= "d78dd6ab1afd4ba7849872cec14d5c6a";
	public static final String APP_SECRET					= "fa59889619da416e9aaef2f28168e820";
	public static final String PRODUCT_KEY					= "90d59aea35794fcab5223348ae2fdaac";
	public static final String PRODUCT_SECRET				= "2f8d602b10184ab1a851b6a91205dc7c";
	public static final String SECRET_KEY					= "1dfe5b59f891289c";
	public static final String TAG_ERROR					= "ERROR";
	
	// Device default information
	public static final String DEVICE_DEFAULT_AP_PREFIX		= "XPG-GAgent-";
	public static final String DEVICE_DEFAULT_AP_PASSWD		= "123456789";
	
	// Device information keys
	public static final String KEY_WIFI_HARDWARE_VERSION	= "wifiHardVersion";
	public static final String KEY_WIFI_SOFTWARE_VERSION	= "wifiSoftVersion";
	public static final String KEY_MCU_HARD_VERSION			= "mcuHardVersion";
	public static final String KEY_MCU_SOFT_VERSION			= "mcuSoftVersion";
	public static final String KEY_WIFI_FIRMWARE_ID			= "wifiFirmwareId";
	public static final String KEY_WIFI_FIRMWARE_VERSION	= "wifiFirmwareVer";
	public static final String KEY_PRODUCT_KEY				= "productKey";
	
	// Push API Key
	
	public static final String URI_MSG_KEY					= "msg";
	
	// User management class instantiate
	private static UserManagement mUserManagement = null;
	public static String clientId = null;
	
	public static UserManagement getUserManagement() {
		return mUserManagement;
	}
	
	public static DeviceManagement getDeviceManagement() {
		return DeviceManagement.getInstance();
	}
	
	public static MessageManagement getMessageManagement() {
		return MessageManagement.getInstance();
	}
	
	public static MediaManagement getMediaManagement() {
		return MediaManagement.getInstance();
	}
	
	public static void setUserManagement(UserManagement user) {
		mUserManagement = user;
		if(mUserManagement == null)
			mUserManagement = new UserManagement();
	}
	
	public static int dp2px(Context context,float dp)
    {
        return (int)(context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
	
	public static Intent setIntentExtras(Intent intent, String key, String value) {
		
		Bundle bundle = intent.getExtras();
		if(bundle == null)
			bundle = new Bundle();
		bundle.putString(key, value);
		intent.putExtras(bundle);
		return intent;
	}
	
	public static Intent setIntentExtras(Intent intent, String[] keys, String[] values) {
		
		Bundle bundle = intent.getExtras();
		if(bundle == null)
			bundle = new Bundle();
		int length = keys.length;
		
		for(int i = 0 ; i < length ; i++) {
			
			if(i < values.length) {
				bundle.putString(keys[i], values[i]);
			} else {
				bundle.putString(keys[i], "");
			}
		}
		
		intent.putExtras(bundle);
		return intent;
	}
	
	public static void initSDKServices(Context context) {

		GizWifiSDK.sharedInstance().startWithAppID(context, APP_ID);
		GizWifiSDK.sharedInstance().setLogLevel(GizLogPrintLevel.GizLogPrintNone);
		clientId = PushManager.getInstance().getClientid(context);
		if(clientId != null && clientId != "") {			
			getUserManagement().setClientId(clientId);
		} else {
			Utils.setToastBottom(context, "Service initialize failed");
		}
	}
	
	public static boolean verifyPhoneNumber(String phone) {
		
		Pattern pat = Pattern.compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
		Matcher match = pat.matcher(phone);
		return match.matches();
	}
	
	public static boolean verifyPassword(String password) {
		
		Pattern pat = Pattern.compile("^[A-Za-z0-9~!@#$%^&*._]{6,16}$");
		Matcher match = pat.matcher(password);
		return match.matches();
		
	}
	
	public static boolean verifyVerifyCode(String code) {
		
		Pattern pat = Pattern.compile("^[0-9]{6}$");
		Matcher match = pat.matcher(code);
		return match.matches();
	}
	
	public static boolean verifyUsername(String username) {
		
		return verifyPhoneNumber(username);
	}
	
	public static boolean verifyEmail(String email) {
		
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern pat = Pattern.compile(str);
        Matcher match = pat.matcher(email);
        return match.matches();
	}
	
	public static void setToastCenter(Context context, String text) {
		
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	public static void setToastCenter(Context context, int resid) {
		
		String text = context.getResources().getString(resid);
		setToastCenter(context, text);
	}
	
	public static void setToastBottom(Context context, String text) {
		
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void setToastBottom(Context context, int resid) {
		
		String text = context.getResources().getString(resid);
		setToastBottom(context, text);
	}
	
	public static void showErrorOnToast(Context context,GizWifiErrorCode result) {
		
		if(result == GizWifiErrorCode.GIZ_SDK_CONNECTION_TIMEOUT) {
			
			//Time out
			setToastCenter(context, R.string.toast_error_timeout);
		} else if(result == GizWifiErrorCode.GIZ_SDK_CONNECTION_ERROR) {
			
			//Connection error
			setToastCenter(context, R.string.toast_error_connection_failed);
		} else if(result == GizWifiErrorCode.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR
				|| result == GizWifiErrorCode.GIZ_OPENAPI_USER_NOT_EXIST) {
			
			//User name or password error
			setToastCenter(context, R.string.toast_login_failed_wrong);
		} else if(result == GizWifiErrorCode.GIZ_OPENAPI_PHONE_UNAVALIABLE) {
			
			//User already existed
			setToastCenter(context, R.string.toast_register_fail_existed);
		} else if(result == GizWifiErrorCode.GIZ_OPENAPI_TOKEN_EXPIRED || 
					result == GizWifiErrorCode.GIZ_OPENAPI_TOKEN_INVALID) {
			
			setToastCenter(context, R.string.toast_error_token_expired);
		} else {
			
			//Unknown error
			String text = context.getResources().getString(R.string.toast_error_unknown);
			setToastCenter(context, text + " :\n" + result);
		}
	}
	/** 
     * �õ��豸��Ļ�Ŀ�� 
     */  
    public static int getScreenWidth(Context context) {  
        return context.getResources().getDisplayMetrics().widthPixels;  
    }  
  
    /** 
     * �õ��豸��Ļ�ĸ߶� 
     */  
    public static int getScreenHeight(Context context) {  
        return context.getResources().getDisplayMetrics().heightPixels;  
    }  
  
    /** 
     * �õ��豸���ܶ� 
     */  
    public static float getScreenDensity(Context context) {  
        return context.getResources().getDisplayMetrics().density;  
    }
    
    @SuppressLint("SimpleDateFormat")
	public static String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }
    
    public static byte[] BitmapToBytes(Bitmap bitmap) {
    	byte[] ret = null;
    	try {    		
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    		bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
    		ret = baos.toByteArray();
    		baos.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return ret;
    }
    
    public static String getDateFromUnix(long time) {
    	Timestamp stamp = new Timestamp(time);
    	return stamp.toString().substring(0,19);
    }
}
