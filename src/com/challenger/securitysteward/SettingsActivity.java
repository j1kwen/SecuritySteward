package com.challenger.securitysteward;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.SDKUtils;
import com.challenger.securitysteward.utils.SDKUtils.OnReceivedResult;
import com.challenger.securitysteward.utils.Utils;

public class SettingsActivity extends BaseActivity implements OnReceivedResult {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initEventListener();
		
		
	}
	
	@Override
	protected void initView() {
		
		mTopBar = (TopBar)findViewById(R.id.topbar_setting);
		mBundle = getIntent().getExtras();
		
		mValueLeftText = mBundle.getString(Utils.KEY_BACK_TEXT);
		mTopBar.setLeftText(mValueLeftText);
	}
	
	@Override
	protected void initEventListener() {
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				// No button
				
			}
			
			@Override
			public void onLeftClick(View v) {
				
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_cl_message)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(R.string.alert_title_confirm)
				.setMessage(R.string.alert_message_clear_all)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						boolean res = Utils.getMessageManagement().clearAll();
						if(res) {
							Utils.setToastBottom(SettingsActivity.this, R.string.toast_text_success);
						} else {
							Utils.setToastBottom(SettingsActivity.this, R.string.toast_error_unknown);
						}
					}
				})
				.setNegativeButton(R.string.alert_button_no, null)
				.create().show();
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_help)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this, WebBrowserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", getString(R.string.item_help));
				bundle.putString("url", "http://help.dogest.cn");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		((RelativeLayout)findViewById(R.id.re_about)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});
		((RelativeLayout)findViewById(R.id.re_check_update)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SDKUtils.getInstance().getUpdateXmlFile(SettingsActivity.this);
			}
		});
	}

	@Override
	public void onReceived(String result) {
		String par = "(?<=android:versionName=\")([0-9]*[\\.]*)*";
		
		Pattern pattern = Pattern.compile(par);
		Matcher match = pattern.matcher(result);
		while(match.find()) {
			String res = match.group();
			checkUpdate(res);
			return;
		}
		Utils.setToastBottom(this, R.string.toast_new_version_err);
	}
	
	private void checkUpdate(String newVer) {
		String now = null;
		try {
	        PackageManager pm = getPackageManager();
	        PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
	        now = pi.versionName;
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
		if(now == null) {
			Utils.setToastBottom(this, R.string.toast_local_version_err);
		}
		Log.d("VERSION","now:" + now + " new:" + newVer);
		if(isNewVersion(now, newVer)) {
			
			final String newVer_f = newVer.replace('.', '_');
			new AlertDialog.Builder(this)
				.setTitle(R.string.alert_title_new_ver)
				.setMessage(String.format(getString(R.string.alert_new_version), now, newVer))
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);  
				          
					    Uri uri = Uri.parse("https://raw.githubusercontent.com/763461297/SecuritySteward/master/bin/SecuritySteward.apk");  
					    Request request = new Request(uri);  
					  
					    //设置允许使用的网络类型，这里是移动网络和wifi都可以    
					    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);    
					    request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					    request.setVisibleInDownloadsUi(true);
					    request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS,"SecuritySteward_"+ newVer_f +".apk");
					    request.setMimeType("application/vnd.android.package-archive");
					    downloadManager.enqueue(request);  
					    Utils.setToastBottom(SettingsActivity.this, R.string.toast_now_downloading);
					}
				})
				.setNegativeButton(R.string.alert_button_no, null)
				.show();
		} else {
			Utils.setToastBottom(this, R.string.toast_is_least_ver);
		}
	}
	
	private boolean isNewVersion(String nowV, String newV) {
		String[] nows = nowV.split("\\.");
		String[] news = newV.split("\\.");
		if(nows.length != news.length) {
			return false;
		}
		for(int i = 0 ; i < nows.length ; i++) {
			if(Integer.valueOf(news[i]) > Integer.valueOf(nows[i])) {
				return true;
			}
		}
		return false;
	}
}
