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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.SDKUtils;
import com.challenger.securitysteward.utils.SDKUtils.OnReceivedResult;
import com.challenger.securitysteward.utils.Utils;

public class BindDeviceActivity extends BaseActivity implements OnReceivedResult {
	
	private Button mButtonBind;
	private EditText mEditTextDid, mEditTextName;
	private String bindDeviceQRCode;
	private RelativeLayout mRelaBindImage;
	private ImageView mImageBind;
	private boolean isDefault;
	
	private String mImageName;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static String defaultPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bind_device);
		
		defaultPath = Environment.getExternalStorageDirectory() + File.separator + "GizWifiSDK/cache/";
		File destDir = new File(defaultPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		
		initView();
		initEventListener();
	}
	
	@Override
	protected void initView() {
		isDefault = true;
		mBundle = getIntent().getExtras();
		mTopBar = (TopBar)findViewById(R.id.topbar_bind);
		mButtonBind = (Button)findViewById(R.id.bind_ok);
		mEditTextDid = (EditText)findViewById(R.id.bind_did);
		mEditTextName = (EditText) findViewById(R.id.bind_name);
		mRelaBindImage = (RelativeLayout) findViewById(R.id.re_bind_image);
		mImageBind = (ImageView) findViewById(R.id.iv_bind_image);
		
		mValueLeftText = getResources().getString(R.string.topbar_back_default);

		mTopBar.setLeftText(mValueLeftText);
		
		try {
			bindDeviceQRCode = mBundle.getString(Utils.KEY_QRCODE_STRING);
			JSONObject json = new JSONObject(bindDeviceQRCode);
			String did = json.getString(Utils.KEY_QRCODE_DID);
			mEditTextDid.setText(did);
		} catch (Exception e) {
			mEditTextDid.setText("");
		}
	}
	
	@Override
	protected void initEventListener() {
		
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				// Nothing
				
			}
			
			@Override
			public void onLeftClick(View v) {
				// Back
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		mButtonBind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String did = mEditTextDid.getText().toString();
				Log.d("DEBUG","did => " + did);
				if(did == null || did.isEmpty()) {
					Utils.setToastCenter(getApplicationContext(), R.string.toast_bind_no_did);
					return;
				}
				
				String name = mEditTextName.getText().toString();
				if(name == null || name.isEmpty()) {
					name = getString(R.string.text_bind_name_default);
				}
				Bitmap image = null;
				if(!isDefault) {
					mImageBind.setDrawingCacheEnabled(true);
					image = Bitmap.createBitmap(mImageBind.getDrawingCache());
					mImageBind.setDrawingCacheEnabled(false);
				}

				
				SDKUtils.getInstance().bindDevice(Utils.SECRET_KEY, Utils.clientId,
						did, name, image, BindDeviceActivity.this);
			}
		});
		
		mRelaBindImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPhotoDialog();
			}
		});
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

            	new AlertDialog.Builder(BindDeviceActivity.this)
            		.setMessage(getString(R.string.popup_item_set_defalut) + "?")
            		.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
			            	mImageBind.setImageBitmap(
			            			BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			            	isDefault = true;
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
            	Log.i("INFO","image has been setted");
                ((ImageView)findViewById(R.id.iv_bind_image)).setImageBitmap(bitmap);
                isDefault = false;
                break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

	@Override
	public void onReceived(String result) {
		String val = result;
        try {
			JSONObject json = new JSONObject(val);
			if(json.getInt("result") == 0) {
				Utils.setToastBottom(BindDeviceActivity.this, R.string.toast_bind_success);
				setResult(RESULT_OK);
				finish();
			} else if(json.getInt("result") == 0x02) {
				Utils.setToastCenter(BindDeviceActivity.this, R.string.toast_bind_device_already);
			} else {
				Utils.setToastCenter(BindDeviceActivity.this, R.string.toast_error_unknown);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Utils.setToastCenter(BindDeviceActivity.this, R.string.toast_error_timeout);
		}
	}
}
