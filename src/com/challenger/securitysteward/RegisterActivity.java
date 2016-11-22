package com.challenger.securitysteward;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity {
	
	private Button mButtonRegister;
	private EditText mEditTextVerifyCode;
	private EditText mEditTextPwd;
	private EditText mEditTextPwdConfirm;
	private TextView mTextViewHint;
	private String mPhoneNumber;
	private String mToAction;
	private String mButtonTextRestore = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		initView();
		initEventListener();
	}
	
	@Override
	protected void initView() {
		
		mBundle = getIntent().getExtras();
		mTopBar = (TopBar)findViewById(R.id.topbar_register);
		mButtonRegister = (Button)findViewById(R.id.register_form_btn_submit);
		mEditTextVerifyCode = (EditText)findViewById(R.id.register_form_verify_code);
		mEditTextPwd = (EditText)findViewById(R.id.register_form_pwd);
		mEditTextPwdConfirm = (EditText)findViewById(R.id.register_form_pwd_confirm);
		mTextViewHint = (TextView)findViewById(R.id.register_verify_phone);
		
		mPhoneNumber = mBundle.getString(Utils.KEY_USER_PHONE_NUMBER);
		mTextViewHint.append(mPhoneNumber);
		mToAction = mBundle.getString(Utils.KEY_ACTION_TO);
		
		try {
			
			mValueLeftText = mBundle.getString(Utils.KEY_BACK_TEXT);
		} catch (Exception e) {
			
			mValueLeftText = getResources().getString(R.string.topbar_back_default);
		}
		mTopBar.setLeftText(mValueLeftText);
		
		if(mToAction.equals(Utils.VALUE_TO_FORGET)) {
			
			// Forget
			mTopBar.setTitleText(getResources().getString(R.string.topbar_title_reset_password));
			mEditTextPwd.setHint(R.string.hint_reset_new_password);
			mEditTextPwdConfirm.setHint(R.string.hint_modify_pwd_new_confirm);
			mButtonRegister.setText(R.string.btn_next);
		}
	}
	
	@Override
	protected void initEventListener() {
		
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				
				//No Button
			}
			
			@Override
			public void onLeftClick(View v) {
				
				//Back
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		mButtonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String username = mPhoneNumber;
				String password = mEditTextPwd.getText().toString();
				String code = mEditTextVerifyCode.getText().toString();
				
				// Verify Code
				if(!Utils.verifyVerifyCode(code)) {
					
					Utils.setToastCenter(RegisterActivity.this, R.string.toast_input_format_verify_code);
					mEditTextVerifyCode.requestFocus();
					return;
				}
				
				// Password
				if(!Utils.verifyPassword(password)) {
					
					Utils.setToastCenter(RegisterActivity.this, R.string.toast_input_format_pwd);
					mEditTextPwd.requestFocus();
					return;
				}
				
				// Confirm Password
				if(!toVerificationUserInput()) {

					Utils.setToastCenter(RegisterActivity.this, R.string.toast_register_fail_mismatch);
					mEditTextPwdConfirm.requestFocus();
					return;
				}
				
				if(mToAction.equals(Utils.VALUE_TO_REGISTER)) {
					
					// Register
					GizWifiSDK.sharedInstance().registerUser(username, password, code, getRegisterType());
				} else {
					
					// Reset Password
					GizWifiSDK.sharedInstance().resetPassword(username, code, password,
															Utils.getUserManagement().getUserType());
				}
				
				//Change button status
				mButtonTextRestore = mButtonRegister.getText().toString();
				mButtonRegister.setText(getResources().getString(R.string.btn_waiting));
				mButtonRegister.setEnabled(false);
			}
		});
		
	}
	
	private GizUserAccountType getRegisterType() {
		
		// TODO : Get difference type
		return GizUserAccountType.GizUserPhone;
	}
	
	private boolean toVerificationUserInput() {
		
		// Verification password
		if(!mEditTextPwd.getText().toString()
				.equals(mEditTextPwdConfirm.getText().toString())) {
			return false;
		}
		return true;
	}
	
	@Override
	protected void didRegisterUser(GizWifiErrorCode result, String uid, String token) {
		
		//Change button status
		mButtonRegister.setText(mButtonTextRestore);
		mButtonRegister.setEnabled(true);
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			//Success
			Utils.setToastBottom(getApplicationContext(), R.string.toast_register_success);
			setResult(Utils.RESULT_USER_REGISTER_SUCCESS);
			finish();
			
		} else if(result == GizWifiErrorCode.GIZ_OPENAPI_CODE_INVALID) {
			
			//Code error
			Utils.setToastCenter(RegisterActivity.this, R.string.toast_register_fail_code_invalid);
			mEditTextVerifyCode.requestFocus();
		} else {
			
			//Show error
			Utils.showErrorOnToast(RegisterActivity.this, result);
		}
	}
	
	@Override
	protected void didChangeUserPassword(GizWifiErrorCode result) {
		
		//Update button status
		mButtonRegister.setText(mButtonTextRestore);
		mButtonRegister.setEnabled(true);
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			// Success
			
			setResult(Utils.RESULT_MODIFY_PWD_SUCCESS);
			
			Utils.setToastBottom(getApplicationContext(), R.string.toast_text_success);
			
			finish();
		} else if(result == GizWifiErrorCode.GIZ_OPENAPI_CODE_INVALID) {
			
			//Code error
			Utils.setToastCenter(RegisterActivity.this, R.string.toast_register_fail_code_invalid);
			mEditTextVerifyCode.requestFocus();
		} else {
			
			//Show error
			Utils.showErrorOnToast(RegisterActivity.this, result);
		}
	}
}
