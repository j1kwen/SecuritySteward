package com.challenger.securitysteward;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;

public class AlertDetailsActivity extends BaseActivity {
	
	private TextView mTextViewMessage;
	private TextView mTextViewBody;
	private Button mButtonOK;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert_details);
		
		initView();
		initEventListener();
		
	}
	
	@Override
	protected void initView() {
		mTextViewMessage = (TextView)findViewById(R.id.message_text);
		mTextViewBody = (TextView)findViewById(R.id.message_body);
		mButtonOK = (Button)findViewById(R.id.message_ok);
		mTopBar = (TopBar)findViewById(R.id.topbar_message);
		mBundle = getIntent().getExtras();
		Uri uri = getIntent().getData();
		if(uri != null) {
			String msg = uri.getQueryParameter(Utils.URI_MSG_KEY);
			mTextViewMessage.setText(msg);
		} else {
			mTextViewMessage.setText(mBundle.getString(Utils.KEY_MESSAGE));
		}
		
		mTextViewBody.setText("");
		
		if(null != mBundle.getString(Utils.KEY_MESSAGE_BODY)) {
			mTextViewBody.setText(mBundle.getString(Utils.KEY_MESSAGE_BODY));
		}
		
		mValueLeftText = getResources().getString(R.string.topbar_back_default);

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
				// Back
				finish();
			}
		});
		
		mButtonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
