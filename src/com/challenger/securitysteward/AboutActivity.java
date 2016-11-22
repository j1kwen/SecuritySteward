package com.challenger.securitysteward;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;

public class AboutActivity extends BaseActivity {

	private TextView mTvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initView();
		initEventListener();
	}
	@Override
	protected void initView() {
		mTopBar = (TopBar) findViewById(R.id.topbar_about);
		mTvVersion = (TextView) findViewById(R.id.tv_about_version);
		mTopBar.setLeftText(getString(R.string.topbar_back_default));
		PackageInfo pi = null;
		 
	    try {
	        PackageManager pm = getPackageManager();
	        pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
	        mTvVersion.setText(pi.versionName);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
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
				finish();
			}
		});
	}

}
