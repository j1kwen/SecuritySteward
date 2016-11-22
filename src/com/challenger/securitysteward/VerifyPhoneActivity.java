package com.challenger.securitysteward;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class VerifyPhoneActivity extends BaseActivity {

	private EditText mEditTextPhone;
	private Button mButtonNext;
	private TextView mTextViewLinkLicense;
	private CheckBox mCheckBoxAgree;
	private String mToAction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_phone);
		
		initView();
		initEventListener();
	}
	
	@Override
	protected void initView() {
		
		mTopBar = (TopBar)findViewById(R.id.topbar_verify_phone);
		mBundle = getIntent().getExtras();
		mButtonNext = (Button)findViewById(R.id.verify_phone_submit);
		mEditTextPhone = (EditText)findViewById(R.id.verify_phone_number);
		mTextViewLinkLicense = (TextView)findViewById(R.id.link_license_agreement);
		mCheckBoxAgree = (CheckBox)findViewById(R.id.checkBox_verify_phone_agree);
		mToAction = mBundle.getString(Utils.KEY_ACTION_TO);
		
		try {
			
			mValueLeftText = mBundle.getString(Utils.KEY_BACK_TEXT);
		} catch (Exception e) {
			
			mValueLeftText = getResources().getString(R.string.topbar_back_default);
		}
		mTopBar.setLeftText(mValueLeftText);
		if(mToAction.equals(Utils.VALUE_TO_FORGET)) {
			mTextViewLinkLicense.setVisibility(View.INVISIBLE);
			mCheckBoxAgree.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected void initEventListener() {
		
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				
				//No button
			}
			
			@Override
			public void onLeftClick(View v) {
				
				// Back
				finish();
			}
		});
		
		mButtonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String phone = mEditTextPhone.getText().toString();
				
				if(!Utils.verifyPhoneNumber(phone)) {
					
					Utils.setToastCenter(VerifyPhoneActivity.this, R.string.toast_input_format_phone);
					mEditTextPhone.requestFocus();
					return;
				}
					
				GizWifiSDK.sharedInstance().requestSendPhoneSMSCode(Utils.APP_SECRET, phone);
				
				mButtonNext.setText(getResources().getString(R.string.btn_waiting));
				mButtonNext.setEnabled(false);
			}
		});
		
		mTextViewLinkLicense.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//TODO : Show license agreement
				new AlertDialog.Builder(VerifyPhoneActivity.this)
					.setTitle("License")
					.setMessage("License agreement : none")
					.setPositiveButton("OK", null)
					.create().show();
			}
		});
		
		mCheckBoxAgree.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				mButtonNext.setEnabled(isChecked);
				mButtonNext.setBackgroundResource(isChecked ? 
													R.drawable.button_wide_selector :
													R.color.btn_disable_background);
			}
		});
	}
	
	@Override
	protected void didRequestSendPhoneSMSCode(GizWifiErrorCode result, String token) {
		
		mButtonNext.setText(R.string.btn_next);
		mButtonNext.setEnabled(true);
		
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			
			//Success. Next to register step 2
			
			String mLeftText2 = getResources().getString(R.string.topbar_back_default);
			Intent intent = new Intent(VerifyPhoneActivity.this, RegisterActivity.class);
			startActivityForResult(Utils.setIntentExtras(intent,
					new String[] {Utils.KEY_BACK_TEXT, Utils.KEY_USER_PHONE_NUMBER, Utils.KEY_ACTION_TO},
					new String[] {mLeftText2, mEditTextPhone.getText().toString(), mToAction}),
					Utils.REQUEST_FROM_VERIFY_PHONE);
		} else {
			
			//Show error
			Utils.showErrorOnToast(VerifyPhoneActivity.this, result);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(resultCode) {
			case Utils.RESULT_USER_REGISTER_SUCCESS : {
				
				// Register success and back to activity
				setResult(Utils.RESULT_USER_REGISTER_SUCCESS);
				finish();
				break;
			}
			case Utils.RESULT_MODIFY_PWD_SUCCESS : {
				// Reset success
				setResult(Utils.RESULT_MODIFY_PWD_SUCCESS);
				finish();
				break;
			}
			case RESULT_CANCELED : {
				break;
			}
			default : {
				break;
			}
		}
	}
}
