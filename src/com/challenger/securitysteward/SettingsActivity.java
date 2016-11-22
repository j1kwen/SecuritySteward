package com.challenger.securitysteward;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;

public class SettingsActivity extends BaseActivity {

	
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
				// TODO : to test alert notify
				Intent intent = new Intent(SettingsActivity.this, WebBrowserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", getString(R.string.item_help));
				bundle.putString("url", "http://kf.qq.com/touch/product/mobileqq_app.html?deviceModel=iPhone6%2C2&QUA=V1_IPH_SQ_6.5.9_1_APP_A&platform=15&jailBroken=0&version=6.5.9.426&appid=537048285&scene_id=kf180");
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
	}
}
