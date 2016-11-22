package com.challenger.securitysteward.fragment;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.SettingsActivity;
import com.challenger.securitysteward.UserLoginActivity;
import com.challenger.securitysteward.UserProfileActivity;
import com.challenger.securitysteward.utils.Utils;
import com.gizwits.gizwifisdk.api.GizUserInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserFragment extends BaseFragment {

	private TextView mTextViewUsername;
	private ImageView mImageViewPhoto;
	
	public UserFragment(String title) {
		super(title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		// Third tab - User
    	mCurrentView = inflater.inflate(R.layout.view_user, container, false);
    	
    	mTextViewUsername = (TextView)mCurrentView.findViewById(R.id.tv_name);
    	mImageViewPhoto = (ImageView)mCurrentView.findViewById(R.id.iv_avatar);
  
    	// User profile
    	((RelativeLayout)mCurrentView.findViewById(R.id.re_myinfo)).setOnClickListener(new OnClickListener() {
    		
			@Override
			public void onClick(View v) {
				
				if(Utils.getUserManagement().hasLogin()) {
					// Enter information
					
					Intent intent = Utils.setIntentExtras(new Intent(getContext(),UserProfileActivity.class),
															Utils.KEY_BACK_TEXT,
															mTitle);
					startActivityForResult(intent, Utils.REQUEST_FROM_USER_VIEW);
					return;
				}
				
				// Login
				Intent intent = Utils.setIntentExtras(new Intent(getContext(), UserLoginActivity.class),
													Utils.KEY_BACK_TEXT,
													mTitle);
				startActivityForResult(intent, Utils.REQUEST_FROM_USER_VIEW);
			}
		});
    	
    	// Settings
    	((RelativeLayout)mCurrentView.findViewById(R.id.re_setting)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = Utils.setIntentExtras(new Intent(getContext(), SettingsActivity.class),
													Utils.KEY_BACK_TEXT,
													mTitle);
				startActivityForResult(intent, Utils.REQUEST_FROM_USER_VIEW);
			}
		});
    	
    	// Album
    	((RelativeLayout)mCurrentView.findViewById(R.id.re_album)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!Utils.getUserManagement().hasLogin()) {
					// Login
					((RelativeLayout)mCurrentView.findViewById(R.id.re_myinfo)).performClick();
				}
			}
		});
    	
		return mCurrentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		GizUserInfo userinfo = Utils.getUserManagement().getUserInfo();
		String username = Utils.getUserManagement().getUsername();
		if(username == null || username.equals("") || userinfo == null) {
			username = Utils.getUserManagement().getUsername();
		} else {
			username = Utils.getUserManagement().getUserInfo().getName();
		}
		
		if(Utils.getUserManagement().getUsername() == null) {
			mTextViewUsername.setText(R.string.text_no_user);
			mImageViewPhoto.setImageBitmap(
					BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_useravatar));
		}
		else {
			mTextViewUsername.setText(username);
			Bitmap bitmap = Utils.getUserManagement().getProfilePhoto();
			if(bitmap == null)
				bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_useravatar);
			mImageViewPhoto.setImageBitmap(bitmap);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Utils.RESULT_USER_LOGIN_SUCCESS) {
			
			//User view request
			GizUserInfo userinfo = Utils.getUserManagement().getUserInfo();
			String username = Utils.getUserManagement().getUsername();
			if(username == null || username.equals("") || userinfo == null) {
				username = Utils.getUserManagement().getUsername();
			} else {
				username = Utils.getUserManagement().getUserInfo().getName();
			}

			mTextViewUsername.setText(username);
			
		}
	}
}
