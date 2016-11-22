package com.challenger.securitysteward;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.model.UserManagement;
import com.challenger.securitysteward.utils.Utils;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizUserGenderType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;

public class UserProfileActivity extends BaseActivity {
	
	private TextView mTextViewNickName;
	private TextView mTextViewUserPhone;
	private TextView mTextViewGender;
	private TextView mTextViewRemark;
	private TextView mTextViewBirth; 
	private TextView mTextViewAddress;
	private ImageView mImageViewPhoto;
	private String mImageName;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final int ITEM_REQUEST_NICK = 4;
    private static final int ITEM_REQUEST_ADDRESS = 5;
    private static final int ITEM_REQUEST_REMARK = 6;
    private static String defaultPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		defaultPath = Environment.getExternalStorageDirectory() + File.separator + "GizWifiSDK/cache/";
		File destDir = new File(defaultPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		initView();
		setAdapter();
		initEventListener();
	}
	
	@Override
	protected void initView() {
		
		mTopBar = (TopBar)findViewById(R.id.topbar_user_profile);
		mBundle = getIntent().getExtras();
		mTextViewNickName = (TextView)findViewById(R.id.tv_nick_name);
		mTextViewUserPhone = (TextView)findViewById(R.id.tv_user_phone);
		mTextViewGender = (TextView)findViewById(R.id.tv_sex);
		mTextViewBirth = (TextView)findViewById(R.id.tv_birth);
		mTextViewAddress = (TextView)findViewById(R.id.tv_address);
		mTextViewRemark = (TextView)findViewById(R.id.tv_remark);
		mImageViewPhoto = (ImageView)findViewById(R.id.iv_avatar);
		
		mTopBar.setLeftText(mBundle.getString(Utils.KEY_BACK_TEXT));
	}
	
	private void setAdapter() {
		
		setInfomation();
		GizWifiSDK.sharedInstance().getUserInfo(Utils.getUserManagement().getToken());
	}
	
	private void setInfomation() {
		
		UserManagement mUser = Utils.getUserManagement();
		GizUserInfo mInfo = mUser.getUserInfo();
		if(mInfo == null)
			return;
		
		Bitmap bitmap = mUser.getProfilePhoto();
		if(bitmap == null)
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_useravatar);
		mImageViewPhoto.setImageBitmap(bitmap);
		mTextViewNickName.setText(mInfo.getName());
		mTextViewUserPhone.setText(mUser.getUsername());
		mTextViewGender.setText(mInfo.getUserGender() == GizUserGenderType.GizUserGenderFemale ? 
															R.string.item_gender_female : 
															R.string.item_gender_male);
		mTextViewBirth.setText(mInfo.getBirthday());
		mTextViewAddress.setText(mInfo.getAddress());
		mTextViewRemark.setText(mInfo.getRemark());
	}
	
	private void updateInformation() {
		
		GizUserInfo newInfo = new GizUserInfo();
		if(!mTextViewNickName.getText().toString().equals(""))
			newInfo.setName(mTextViewNickName.getText().toString());
		String female = getString(R.string.item_gender_female);
		newInfo.setUserGender(mTextViewGender.getText().toString().equals(female) ? 
											GizUserGenderType.GizUserGenderFemale :
											GizUserGenderType.GizUserGenderMale);
		if(!mTextViewBirth.getText().toString().equals(""))
			newInfo.setBirthday(mTextViewBirth.getText().toString());
		if(!mTextViewAddress.getText().toString().equals(""))
			newInfo.setAddress(mTextViewAddress.getText().toString());
		if(!mTextViewRemark.getText().toString().equals(""))
			newInfo.setRemark(mTextViewRemark.getText().toString());

		UserManagement user = Utils.getUserManagement();
		
		GizWifiSDK.sharedInstance().changeUserInfo(user.getToken(),
												null,
												null,
												GizUserAccountType.GizUserNormal,
												newInfo);
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
		
    	((RelativeLayout)findViewById(R.id.re_avatar)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPhotoDialog();
			}
		});
		
    	((RelativeLayout)findViewById(R.id.re_nick_name)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserProfileActivity.this, ModifyItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Utils.KEY_ITEM_TITLE, getString(R.string.item_nick_name));
				bundle.putString(Utils.KEY_ITEM_STRING, mTextViewNickName.getText().toString());
				bundle.putInt(Utils.KEY_ITEM_MAX_LEN, 20);
				intent.putExtras(bundle);
				startActivityForResult(intent, ITEM_REQUEST_NICK);
			}
		});
    	
    	((RelativeLayout)findViewById(R.id.re_sex)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showGenderSelector();
			}
		});
    	
    	((RelativeLayout)findViewById(R.id.re_birth)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});
    	
    	((RelativeLayout)findViewById(R.id.re_address)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserProfileActivity.this, ModifyItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Utils.KEY_ITEM_TITLE, getString(R.string.item_address));
				bundle.putString(Utils.KEY_ITEM_STRING, mTextViewAddress.getText().toString());
				bundle.putInt(Utils.KEY_ITEM_MAX_LEN, 40);
				intent.putExtras(bundle);
				startActivityForResult(intent, ITEM_REQUEST_ADDRESS);
			}
		});
    	
    	((RelativeLayout)findViewById(R.id.re_remark)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserProfileActivity.this, ModifyItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Utils.KEY_ITEM_TITLE, getString(R.string.item_remark));
				bundle.putString(Utils.KEY_ITEM_STRING, mTextViewRemark.getText().toString());
				bundle.putInt(Utils.KEY_ITEM_MAX_LEN, 80);
				intent.putExtras(bundle);
				startActivityForResult(intent, ITEM_REQUEST_REMARK);
			}
		});
    	
    	((RelativeLayout)findViewById(R.id.re_modify_pwd)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String token = Utils.getUserManagement().getToken();
				Intent intent = Utils.setIntentExtras(new Intent(UserProfileActivity.this, ModifyPasswordActivity.class),
													Utils.KEY_CURRENT_USER_TOKEN,
													token);
				startActivityForResult(intent, Utils.REQUEST_FROM_USER_VIEW);
			}
		});
    	
		((RelativeLayout)findViewById(R.id.re_logout)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(UserProfileActivity.this)
					.setTitle(R.string.alert_title_confirm)
					.setMessage(R.string.alert_message_logout)
					.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Utils.getUserManagement().logout(true);
							saveUserInfo();
							finish();
						}
					})
					.setNegativeButton(R.string.alert_button_no, null)
					.create().show();
			}
		});
	}
	
	@Override
	protected void didGetUserInfo(GizWifiErrorCode result, GizUserInfo userInfo) {
		
		super.didGetUserInfo(result, userInfo);
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS)
			setInfomation();
	}
	
	@Override
	protected void didChangeUserInfo(GizWifiErrorCode result) {
		
		super.didChangeUserInfo(result);
		if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
			GizWifiSDK.sharedInstance().getUserInfo(Utils.getUserManagement().getToken());
		}
		else {
			setInfomation();
			Utils.showErrorOnToast(UserProfileActivity.this, result);
			Utils.setToastBottom(UserProfileActivity.this, R.string.toast_error_user_info_update_fail);
		}
	}
	
	private void showDatePicker() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alert_date_picker);
        
        TextView tv_title = (TextView)window.findViewById(R.id.tv_title);
        tv_title.setText(R.string.item_birthday);
        
        final DatePicker datePick = (DatePicker)window.findViewById(R.id.date_picker);
        if(!mTextViewBirth.getText().toString().equals("")) {
        	
        	Date mDate = stringToDate(mTextViewBirth.getText().toString());
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(mDate);
        	datePick.init(cal.get(Calendar.YEAR),
        				cal.get(Calendar.MONTH),
        				cal.get(Calendar.DAY_OF_MONTH),
        				null);
        }
        
        TextView tv_ok = (TextView) window.findViewById(R.id.tv_content_btn);
        tv_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int y = datePick.getYear();
				int m = datePick.getMonth() + 1;
				int d = datePick.getDayOfMonth();
				String dateStr = Integer.toString(y) + "-" + Integer.toString(m) + "-" + Integer.toString(d);
				mTextViewBirth.setText(dateStr);
				updateInformation();
				dlg.cancel();
			}
		});
	}
	
	private void showGenderSelector() {
		
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alertdialog);
        
        TextView tv_title = (TextView)window.findViewById(R.id.tv_title);
        tv_title.setText(R.string.item_gender);
        
        TextView tv_takephoto = (TextView) window.findViewById(R.id.tv_content1);
        tv_takephoto.setText(R.string.item_gender_male);
        tv_takephoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	CharSequence text = ((TextView)v).getText();
            	if(!mTextViewGender.getText().equals(text)) {
            		mTextViewGender.setText(text);
            		updateInformation();
            	}
                dlg.cancel();
            }
        });
        
        TextView tv_choose = (TextView) window.findViewById(R.id.tv_content2);
        tv_choose.setText(R.string.item_gender_female);
        tv_choose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	CharSequence text = ((TextView)v).getText();
            	if(!mTextViewGender.getText().equals(text)) {
            		mTextViewGender.setText(text);
            		updateInformation();
            	}
                dlg.cancel();
            }
        });
        
        ((TextView)window.findViewById(R.id.tv_content3)).setVisibility(View.GONE);
	}
	
	
	private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alertdialog);
        TextView tv_title = (TextView)window.findViewById(R.id.tv_title);
        tv_title.setText(R.string.popup_title_photo);
        
        TextView tv_takephoto = (TextView) window.findViewById(R.id.tv_content1);
        tv_takephoto.setText(R.string.popup_item_take_photo);
        tv_takephoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	mImageName = Utils.getNowTime() + ".png";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(defaultPath, mImageName)));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                dlg.cancel();
            }
        });
        
        TextView tv_choose = (TextView) window.findViewById(R.id.tv_content2);
        tv_choose.setText(R.string.popup_item_choose_photo);
        tv_choose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	mImageName = Utils.getNowTime() + ".png";
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                dlg.cancel();
            }
        });

        TextView tv_default = (TextView) window.findViewById(R.id.tv_content3);
        tv_default.setText(R.string.popup_item_set_defalut);
        tv_default.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	new AlertDialog.Builder(UserProfileActivity.this)
            		.setMessage(getString(R.string.popup_item_set_defalut) + "?")
            		.setPositiveButton(R.string.alert_button_yes,new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utils.getUserManagement().setProfilePhoto(null);
			            	mImageViewPhoto.setImageBitmap(
			            			BitmapFactory.decodeResource(getResources(), R.drawable.default_useravatar));
			            	saveUserInfo();
						}
					})
            		.setNegativeButton(R.string.alert_button_no, null)
            		.create().show();
            	
                dlg.cancel();
            }
        });
    }
	
	private void startPhotoZoom(Uri uri1, int size) {
		Log.i("INFO","img has been send to zoom");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(defaultPath, mImageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        	
            switch (requestCode) {
            
            case PHOTO_REQUEST_TAKEPHOTO:

                startPhotoZoom(
                        Uri.fromFile(new File(defaultPath, mImageName)),
                        480);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), 480);
                break;

            case PHOTO_REQUEST_CUT:
            	
            	Bitmap bitmap = BitmapFactory.decodeFile(defaultPath
                        + mImageName);
            	Log.i("INFO","avatar has been setted");
                ((ImageView)findViewById(R.id.iv_avatar)).setImageBitmap(bitmap);
                //updateAvatarInServer(mImageName);
                Utils.getUserManagement().setProfilePhoto(bitmap);
                saveUserInfo();
                break;
                
            case ITEM_REQUEST_NICK:
            	
            	String nick = data.getExtras().getString(Utils.KEY_ITEM_STRING);
            	mTextViewNickName.setText(nick);
            	updateInformation();
            	break;

			case ITEM_REQUEST_ADDRESS:
				
				String address = data.getExtras().getString(Utils.KEY_ITEM_STRING);
				mTextViewAddress.setText(address);
				updateInformation();
				break;
				
			case ITEM_REQUEST_REMARK:
	
				String remark = data.getExtras().getString(Utils.KEY_ITEM_STRING);
				mTextViewRemark.setText(remark);
				updateInformation();
			    break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        } else if(resultCode == Utils.RESULT_MODIFY_PWD_SUCCESS) {
        	
        	Utils.getUserManagement().logout(false);
        	saveUserInfo();
        	UserProfileActivity.this.finish();
        }
    }
}
