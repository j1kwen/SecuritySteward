package com.challenger.securitysteward.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;

public class UserManagement implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7259380391819826498L;
	private String mUsername = null;
	private String mUserPwd = null;
	private String mCurrentUid = null;
	private String mCurrentToken = null;
	private boolean mHasLogin = false;
	private GizUserAccountType mUserType;
	private GizUserInfo mUserInfo = null;
	private byte[] mProfilePhoto;
	private String cid = null;
	
	public UserManagement() {
		mHasLogin = false;
		mUserType = GizUserAccountType.GizUserPhone;
		mProfilePhoto = null;
	}
	
	public void login(String username,String password, String uid, String token) {
		mUsername = username;
		mUserPwd = password;
		mCurrentUid = uid;
		mCurrentToken = token;
		mHasLogin = true;
	}
	

	public void logout(boolean delavatar) {
		mUsername = null;
		mUserPwd = null;
		mUserInfo = null;
		mCurrentToken = null;
		if(delavatar)
			mCurrentUid = null;
		mHasLogin = false;
		if(delavatar)
			mProfilePhoto = null;
	}
	
	public void setClientId(String cid) {
		this.cid = cid;
	}
	
	public String getClientId() {
		return cid;
	}
	
	public void setUserInfo(GizUserInfo userinfo) {
		mUserInfo = userinfo;
	}
	
	public void setProfilePhoto(Bitmap bitmap) {
		
		if(bitmap == null) {
			mProfilePhoto = null;
			return;
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		    bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
			mProfilePhoto = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public GizUserInfo getUserInfo() {
		return mUserInfo;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public String getUserPassword() {
		return mUserPwd;
	}
	
	public String getUid() {
		return mCurrentUid;
	}
	
	public String getToken() {
		return mCurrentToken;
	}
	
	public boolean hasLogin() {
		return mHasLogin;
	}
	
	public Bitmap getProfilePhoto() {
		if(mProfilePhoto == null)
			return null;
		return BitmapFactory.decodeByteArray(mProfilePhoto, 0, mProfilePhoto.length);
	}
	
	public GizUserAccountType getUserType() {
		return mUserType;
	}
}
