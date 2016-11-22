package com.challenger.securitysteward;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserLoginActivity extends BaseActivity {
	
	//Some controls like TopBar etc are declaration on super class
	private Button mButtonLogin;
	private EditText mEditTextUser;
	private EditText mEditTextPwd;
	private TextView mTextViewForget;
	private TextView mTextViewRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		
		initView();
		initEventListener();
		
	}
	
	@Override
	protected void initView() {
		
		mTopBar = (TopBar)findViewById(R.id.topbar_user_login);
		mButtonLogin = (Button)findViewById(R.id.login_form_btn_submit);
		mEditTextUser = (EditText)findViewById(R.id.login_form_user);
		mEditTextPwd = (EditText)findViewById(R.id.login_form_pwd);
		mTextViewForget = (TextView)findViewById(R.id.link_forget_password);
		mTextViewRegister = (TextView)findViewById(R.id.link_register_new);
		mBundle = getIntent().getExtras();
		try {
			
			mValueLeftText = mBundle.getString(Utils.KEY_BACK_TEXT);
		} catch (Exception e) {
			
			mValueLeftText = getResources().getString(R.string.topbar_back_default);
		}
		if(mValueLeftText == null || mValueLeftText.equals(""))
			mValueLeftText = getResources().getString(R.string.topbar_back_default);
		mTopBar.setLeftText(mValueLeftText);
	}
	
	@Override
	protected void initEventListener() {
		
		//TopBar event listener
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				
				//No button
			}
			
			@Override
			public void onLeftClick(View v) {
				
				//Back
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		mTextViewForget.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Forget password
				String value = mTopBar.getTitleText();
				Intent intent = Utils.setIntentExtras(new Intent(UserLoginActivity.this, VerifyPhoneActivity.class),
											new String[] {Utils.KEY_BACK_TEXT, Utils.KEY_ACTION_TO},
											new String[] {value, Utils.VALUE_TO_FORGET});
				startActivityForResult(intent, Utils.REQUEST_FROM_FORGET);
			}
		});
		
		mTextViewRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Register
				String value = mTopBar.getTitleText();
				Intent intent = Utils.setIntentExtras(new Intent(UserLoginActivity.this, VerifyPhoneActivity.class),
						new String[] {Utils.KEY_BACK_TEXT, Utils.KEY_ACTION_TO},
						new String[] {value, Utils.VALUE_TO_REGISTER});
				startActivityForResult(intent, Utils.REQUEST_FROM_USER_LOGIN);
			}
		});
		
		//Login button event listener
		mButtonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String username = mEditTextUser.getText().toString();
				String password = mEditTextPwd.getText().toString();
				if(!Utils.verifyUsername(username)) {
					
					Utils.setToastCenter(UserLoginActivity.this, R.string.toast_input_format_user);
					mEditTextUser.requestFocus();
					return;
				}
				
				if(!Utils.verifyPassword(password)) {

					Utils.setToastCenter(UserLoginActivity.this, R.string.toast_input_format_pwd);
					mEditTextPwd.requestFocus();
					return;
				}
				
				mCurUser = username;
				mCurPwd = password;
				GizWifiSDK.sharedInstance().userLogin(username, password);
				
				//Change button status
				mButtonLogin.setText(getResources().getString(R.string.btn_login_processing));
				mButtonLogin.setEnabled(false);
			}
		});
		
	}
	
	@Override
	protected void didUserLogin(GizWifiErrorCode result, String uid, String token) {
		
		super.didUserLogin(result, uid, token);
		//Update button status
		mButtonLogin.setText(getResources().getString(R.string.btn_login));
		mButtonLogin.setEnabled(true);
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			// Success, Get uid and token
			
			GizWifiSDK.sharedInstance().getUserInfo(token);

		} else {
			
			//Show error
			Utils.showErrorOnToast(UserLoginActivity.this, result);
		}
	}
	
	@Override
	protected void didGetUserInfo(GizWifiErrorCode result, GizUserInfo userInfo) {
		super.didGetUserInfo(result, userInfo);
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			setResult(Utils.RESULT_USER_LOGIN_SUCCESS);
			Utils.setToastBottom(getApplicationContext(), R.string.toast_login_success);
			finish();
		}
	}
	
}
