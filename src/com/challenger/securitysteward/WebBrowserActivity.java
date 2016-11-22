package com.challenger.securitysteward;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;

public class WebBrowserActivity extends BaseActivity {

	private WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_browser);
		initView();
		initEventListener();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initView() {
		mTopBar = (TopBar) findViewById(R.id.topbar_web);
		mBundle = getIntent().getExtras();
		String topTitle = "";
		try {
			topTitle = mBundle.getString("title");
		} catch (Exception e) {}
		mTopBar.setTitleText(topTitle);
		mTopBar.setLeftText(getString(R.string.topbar_back_default));
		mWebView = (WebView) findViewById(R.id.web_view);
		mWebView.getSettings().setJavaScriptEnabled(true);
		String url = "about:blank";
		try {
			url = mBundle.getString("url");
		} catch (Exception e) {}
		mWebView.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if( url.startsWith("http:") || url.startsWith("https:") ) {  
	                view.loadUrl(url);
					return true;  
	            }
	            return false;  
			}
		});
		mWebView.loadUrl(url);
		Log.d("Web View","load url : " + url);
	}

	@Override
	protected void initEventListener() {
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				// Refresh
				mWebView.reload();
			}
			
			@Override
			public void onLeftClick(View v) {
				if(mWebView.canGoBack()) {
					mWebView.goBack();
				} else {
					finish();
				}
			}
		});
	}

}
