package com.challenger.securitysteward;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ModifyItemActivity extends BaseActivity {

	private String mCurItemString;
	private EditText mEditTextItem;
	private TextView mTextViewLast;
	private int mLimitLength;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_item);
		
		initView();
		initEventListener();
	}
	
	@Override
	protected void initView() {
		
		mTopBar = (TopBar)findViewById(R.id.topbar_modify_item);
		mEditTextItem = (EditText)findViewById(R.id.modify_item);
		mTextViewLast = (TextView)findViewById(R.id.tv_last_length);
		mBundle = getIntent().getExtras();
		
		mLimitLength = mBundle.getInt(Utils.KEY_ITEM_MAX_LEN, 50);
		String mTitle = mBundle.getString(Utils.KEY_ITEM_TITLE);
		mCurItemString = mBundle.getString(Utils.KEY_ITEM_STRING);
		
		
		mEditTextItem.setText(mCurItemString);
		mEditTextItem.setHint(mTitle);
		mEditTextItem.setSelection(mCurItemString.length());
		mTextViewLast.setText(Integer.toString(mLimitLength - mCurItemString.length()));
		mTopBar.setLeftText(getString(R.string.topbar_back_default));
		mTopBar.setTitleText(mTitle);
	}
	
	@Override
	protected void initEventListener() {
		
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				// OK
				String mText = mEditTextItem.getText().toString();
				if(mText.length() > mLimitLength) {
					
					Utils.setToastCenter(ModifyItemActivity.this, R.string.toast_input_limit_exceeded);
					return;
				}
				if(mText.length() == 0) {
					
					Utils.setToastCenter(ModifyItemActivity.this, R.string.toast_input_empty);
					return;
				}
				if(mText.equals(mCurItemString)) {
					// Unmodified
					setResult(RESULT_CANCELED);
				} else {
					// OK
					Intent intent = getIntent();
					intent = Utils.setIntentExtras(intent, Utils.KEY_ITEM_STRING, mText);
					setResult(RESULT_OK, intent);
				}
				finish();
			}
			
			@Override
			public void onLeftClick(View v) {
				// Back
				if(!mCurItemString.equals(mEditTextItem.getText().toString())) {
					new AlertDialog.Builder(ModifyItemActivity.this)
						.setMessage(getString(R.string.alert_message_not_saved))
						.setPositiveButton(R.string.alert_button_quit, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								setResult(RESULT_CANCELED);
								finish();
							}
						})
						.setNegativeButton(R.string.alert_button_cancel, null)
						.create().show();
						
				} else {
					setResult(RESULT_CANCELED);
					finish();
				}
			}
		});
		
		mEditTextItem.addTextChangedListener(new TextWatcher() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int len = s.length();
				mTextViewLast.setText(Integer.toString(mLimitLength - len));
				if(len > mLimitLength)
					mTextViewLast.setTextColor(getResources().getColor(R.color.item_limit_exceeded));
				else
					mTextViewLast.setTextColor(getResources().getColor(R.color.item_edit_text));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
