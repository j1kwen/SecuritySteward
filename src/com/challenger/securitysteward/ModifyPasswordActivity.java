package com.challenger.securitysteward;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ModifyPasswordActivity extends BaseActivity {

	private Button mButtonNext;
	private EditText mEditTextOldPwd;
	private EditText mEditTextNewPwd;
	private EditText mEditTextNewPwdConfirm;
	private TextView mTextViewForget;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		
		initView();
		initEventListener();
	}
	
	@Override
	protected void initView() {
		
		mBundle = getIntent().getExtras();
		mTopBar = (TopBar)findViewById(R.id.topbar_modify_pwd);
		mButtonNext = (Button)findViewById(R.id.modify_pwd_next);
		mEditTextOldPwd = (EditText)findViewById(R.id.modify_pwd_old);
		mEditTextNewPwd = (EditText)findViewById(R.id.modify_pwd_new);
		mEditTextNewPwdConfirm = (EditText)findViewById(R.id.modify_pwd_new_confirm);
		mTextViewForget = (TextView)findViewById(R.id.link_forget_password);
		
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
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		mButtonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//TODO verify
				String token = mBundle.getString(Utils.KEY_CURRENT_USER_TOKEN);
				String oldPassword = mEditTextOldPwd.getText().toString();
				String newPassword = mEditTextNewPwd.getText().toString();
				String newPasswordConfirm = mEditTextNewPwdConfirm.getText().toString();
				
				// Old Password
				if(!Utils.verifyPassword(oldPassword)) {
					
					Utils.setToastCenter(ModifyPasswordActivity.this, R.string.toast_input_format_pwd_old);
					mEditTextOldPwd.requestFocus();
					return;
				}
				
				// New Password
				if(!Utils.verifyPassword(newPassword)) {
					
					Utils.setToastCenter(ModifyPasswordActivity.this, R.string.toast_input_format_pwd_new);
					mEditTextNewPwd.requestFocus();
					return;
				}
				
				// Confirm Password
				if(!newPassword.equals(newPasswordConfirm)) {

					Utils.setToastCenter(ModifyPasswordActivity.this, R.string.toast_register_fail_mismatch);
					mEditTextNewPwdConfirm.requestFocus();
					return;
				}
				
				GizWifiSDK.sharedInstance().changeUserPassword(token, oldPassword, newPassword);
				
				//Change button status
				mButtonNext.setText(getResources().getString(R.string.btn_waiting));
				mButtonNext.setEnabled(false);
			}
		});
		
		mTextViewForget.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Forget password
				Intent intent = Utils.setIntentExtras(new Intent(ModifyPasswordActivity.this, VerifyPhoneActivity.class),
											new String[] {Utils.KEY_BACK_TEXT, Utils.KEY_ACTION_TO},
											new String[] {getString(R.string.topbar_back_default), Utils.VALUE_TO_FORGET});
				startActivityForResult(intent, Utils.REQUEST_FROM_FORGET);
			}
		});
	}
	
	@Override
	protected void didChangeUserPassword(GizWifiErrorCode result) {
		
		//Update button status
		mButtonNext.setText(getResources().getString(R.string.btn_next));
		mButtonNext.setEnabled(true);
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			// Success
			
			setResult(Utils.RESULT_MODIFY_PWD_SUCCESS);
			
			Utils.setToastBottom(getApplicationContext(), R.string.toast_text_success);
			
			finish();
		} else {
			
			//Show error
			Utils.showErrorOnToast(ModifyPasswordActivity.this, result);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Utils.RESULT_MODIFY_PWD_SUCCESS) {
			
			setResult(Utils.RESULT_MODIFY_PWD_SUCCESS);
			finish();
		}
	}
}
