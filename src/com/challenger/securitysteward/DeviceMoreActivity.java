package com.challenger.securitysteward;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.model.DeviceItemModel;
import com.challenger.securitysteward.utils.SDKUtils;
import com.challenger.securitysteward.utils.SDKUtils.OnReceivedBytes;
import com.challenger.securitysteward.utils.SDKUtils.OnReceivedResult;
import com.challenger.securitysteward.utils.Utils;

public class DeviceMoreActivity extends BaseActivity implements OnReceivedResult, OnReceivedBytes {
	
	private DeviceItemModel mDeviceInfo = null;
	private ImageView mDeviceImage;
	private TextView mDeviceName, mDeviceNameOnItem, mDeviceDid, mRemark, mLevel;
	
	public static final int MODIFY_NAME = 0x04;
	public static final int MODIFY_REMARK = 0x05;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private int[] levelIds = new int[] {
    		R.string.item_level_1,
    		R.string.item_level_2,
    		R.string.item_level_3};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_more);
		initView();
		initEventListener();
	}

	@Override
	protected void initView() {
		mTopBar = (TopBar) findViewById(R.id.topbar_device_more);
		mDeviceInfo = (DeviceItemModel) getIntent().getSerializableExtra("device");
		mTopBar.setTitleText(mDeviceInfo.getName());
		mTopBar.setLeftText(getString(R.string.topbar_back_default));
		
		mDeviceImage = (ImageView) findViewById(R.id.iv_avatar);
		mDeviceName = (TextView) findViewById(R.id.tv_name);
		mDeviceNameOnItem = (TextView) findViewById(R.id.tv_item_name);
		mDeviceDid = (TextView) findViewById(R.id.tv_did);
		mRemark = (TextView) findViewById(R.id.tv_remark);
		mLevel = (TextView) findViewById(R.id.tv_level);
		
		mLevel.setText(getString(levelIds[(int) (mDeviceInfo.getLevel() - 1)]));
		mRemark.setText(mDeviceInfo.getRemark());
		mDeviceDid.setText(String.format("ID: %s", mDeviceInfo.getDid()));
		mDeviceName.setText(mDeviceInfo.getName());
		mDeviceNameOnItem.setText(mDeviceInfo.getName());
		mDeviceImage.setImageBitmap(Utils.getMediaManagement().getDeviceBitmap(mDeviceInfo.getDid()));
	}

	@Override
	protected void initEventListener() {
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				// NO BUTTON
			}
			
			@Override
			public void onLeftClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_name)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeviceMoreActivity.this, ModifyItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Utils.KEY_ITEM_MAX_LEN, 50);
				bundle.putString(Utils.KEY_ITEM_STRING, mDeviceInfo.getName());
				bundle.putString(Utils.KEY_ITEM_TITLE, getString(R.string.item_alias));
				intent.putExtras(bundle);
				startActivityForResult(intent, MODIFY_NAME);
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_remark)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeviceMoreActivity.this, ModifyItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Utils.KEY_ITEM_MAX_LEN, 150);
				bundle.putString(Utils.KEY_ITEM_STRING, mDeviceInfo.getRemark());
				bundle.putString(Utils.KEY_ITEM_TITLE, getString(R.string.item_remark));
				intent.putExtras(bundle);
				startActivityForResult(intent, MODIFY_REMARK);
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_set_level)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLevelPicker();
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_select_img)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPhotoDialog();
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_clear_msg)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(DeviceMoreActivity.this)
				.setTitle(R.string.alert_title_confirm)
				.setMessage(R.string.alert_message_clear)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						boolean res = Utils.getMessageManagement().clearMessage(mDeviceInfo.getDid());
						if(res) {
							Utils.setToastBottom(DeviceMoreActivity.this, R.string.toast_text_success);
						} else {
							Utils.setToastBottom(DeviceMoreActivity.this, R.string.toast_error_unknown);
						}
					}
				})
				.setNegativeButton(R.string.alert_button_no, null)
				.create().show();
			}
		});
		
		((RelativeLayout)findViewById(R.id.re_unbind)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(DeviceMoreActivity.this)
				.setTitle(R.string.alert_title_confirm)
				.setMessage(R.string.alert_message_unbind)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SDKUtils.getInstance().unbindDevice(Utils.SECRET_KEY, Utils.clientId,
															mDeviceInfo.getDid(), DeviceMoreActivity.this);
					}
				})
				.setNegativeButton(R.string.alert_button_no, null)
				.create().show();
			}
		});

	}

	@Override
	public void onReceived(String result) {
		String val = result;
		try {
			JSONObject json = new JSONObject(val);
			if(json.getInt("result") == 0) {
				Utils.setToastBottom(DeviceMoreActivity.this, R.string.toast_unbind_success);
				setResult(RESULT_OK);
				finish();
			} else if(json.getInt("result") == 10) {
				//
				DeviceItemModel dev = Utils.getDeviceManagement().get(mDeviceInfo.getDid());
				dev.setName(mDeviceName.getText().toString());
				dev.setRemark(mRemark.getText().toString());
				mTopBar.setTitleText(dev.getName());
				SDKUtils.getInstance().getDeviceImage(Utils.SECRET_KEY, Utils.clientId, mDeviceInfo.getDid(), this);
			} else {
				Utils.setToastCenter(DeviceMoreActivity.this, R.string.toast_error_unknown);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Utils.setToastCenter(DeviceMoreActivity.this, R.string.toast_error_timeout);
		}
	}
	
	private String mImageName;
	private String defaultPath = Environment.getExternalStorageDirectory() + File.separator + "GizWifiSDK/cache/";
	
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

            	new AlertDialog.Builder(DeviceMoreActivity.this)
            		.setMessage(getString(R.string.popup_item_set_defalut) + "?")
            		.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
			            	mDeviceImage.setImageBitmap(
			            			BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			            	SDKUtils.getInstance().deleteDeviceImage(Utils.SECRET_KEY, Utils.clientId, mDeviceInfo.getDid());
			            	Utils.getMediaManagement().deleteImageLocal(mDeviceInfo.getDid());
						}
					})
            		.setNegativeButton(R.string.alert_button_no, null)
            		.create().show();
            	
                dlg.cancel();
            }
        });
    }
	
	private void showLevelPicker() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alertdialog);
        
        TextView tv_title = (TextView)window.findViewById(R.id.tv_title);
        tv_title.setText(R.string.item_level);
        
        TextView tv_low = (TextView) window.findViewById(R.id.tv_content1);
        tv_low.setText(R.string.item_level_1);
        tv_low.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	CharSequence text = ((TextView)v).getText();
            	if(!mLevel.getText().equals(text)) {
            		mLevel.setText(text);
            		mDeviceInfo.setLevel(1);
            		updateDeviceLevel();
            	}
                dlg.cancel();
            }
        });
        
        TextView tv_medium = (TextView) window.findViewById(R.id.tv_content2);
        tv_medium.setText(R.string.item_level_2);
        tv_medium.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	CharSequence text = ((TextView)v).getText();
            	if(!mLevel.getText().equals(text)) {
            		mLevel.setText(text);
            		mDeviceInfo.setLevel(2);
            		updateDeviceLevel();
            	}
                dlg.cancel();
            }
        });
        
        TextView tv_high = (TextView) window.findViewById(R.id.tv_content3);
        tv_high.setText(R.string.item_level_3);
        tv_high.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	CharSequence text = ((TextView)v).getText();
            	if(!mLevel.getText().equals(text)) {
            		mLevel.setText(text);
            		mDeviceInfo.setLevel(3);
            		updateDeviceLevel();
            	}
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
                mDeviceImage.setImageBitmap(bitmap);
                DeviceItemModel devImage = new DeviceItemModel();
                devImage.setDid(mDeviceInfo.getDid());
            	SDKUtils.getInstance().setDeviceInfo(Utils.SECRET_KEY, Utils.clientId,
            									 devImage, bitmap, this);
                break;
            case MODIFY_NAME:
            	String name = data.getExtras().getString(Utils.KEY_ITEM_STRING);
            	DeviceItemModel devName = new DeviceItemModel();
            	devName.setDid(mDeviceInfo.getDid());
            	devName.setName(name);
				mDeviceName.setText(name);
				mDeviceNameOnItem.setText(name);
            	SDKUtils.getInstance().setDeviceInfo(Utils.SECRET_KEY, Utils.clientId,
            									 devName, null, this);
            	break;
            case MODIFY_REMARK:
            	String remark = data.getExtras().getString(Utils.KEY_ITEM_STRING);
            	DeviceItemModel devRemark = new DeviceItemModel();
            	devRemark.setDid(mDeviceInfo.getDid());
            	devRemark.setRemark(remark);
				mRemark.setText(remark);
            	SDKUtils.getInstance().setDeviceInfo(Utils.SECRET_KEY, Utils.clientId,
            									 devRemark, null, this);
            	break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
	
	private void updateDeviceLevel() {
		long level = mDeviceInfo.getLevel();
    	DeviceItemModel devLevel = new DeviceItemModel();
    	devLevel.setDid(mDeviceInfo.getDid());
    	devLevel.setLevel(level);
    	SDKUtils.getInstance().setDeviceInfo(Utils.SECRET_KEY, Utils.clientId,
    								devLevel, null, this);
	}

	@Override
	public void onReceivedByte(String did, byte[] bytes) {
		Utils.getMediaManagement().writeImageToLocal(did, bytes);
	}
}
