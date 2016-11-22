package com.challenger.securitysteward;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.utils.Utils;
import com.google.zxing.WriterException;
import com.karics.library.zxing.encode.CodeCreator;

public class WifiConfigerActivity extends BaseActivity {
	
	private Button mButtonQr;
	private EditText mEditTextSsid, mEditTextPwd;
	private CheckBox mCheckBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_info);
		initView();
		initEventListener();
	}
	@Override
	protected void initView() {
		mTopBar = (TopBar) findViewById(R.id.topbar_wifi);
		mEditTextSsid = (EditText)findViewById(R.id.wifi_ssid);
		mEditTextPwd = (EditText) findViewById(R.id.wifi_pwd);
		mButtonQr = (Button) findViewById(R.id.create_qr);
		mCheckBox = (CheckBox) findViewById(R.id.cb_no_pwd);
		mValueLeftText = getResources().getString(R.string.topbar_back_default);

		mTopBar.setLeftText(mValueLeftText);
	}
	@Override
	protected void initEventListener() {
		
		mTopBar.setOnClickListener(new TopBarClickListener() {
			
			@Override
			public void onRightClick(View v) {
				// none
			}
			
			@Override
			public void onLeftClick(View v) {
				finish();
			}
		});
		
		mCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEditTextPwd.setEnabled(!mCheckBox.isChecked());
			}
		});
		
		mButtonQr.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String ssid = mEditTextSsid.getText().toString();
				String pwd = mEditTextPwd.getText().toString();
				if(mCheckBox.isChecked()) {
					pwd = "";
				}
				if(ssid == null || ssid.length() < 1) {
					Utils.setToastCenter(WifiConfigerActivity.this, R.string.toast_wifi_info_err);
					return;
				} else if(!mCheckBox.isChecked() && (pwd == null || pwd.length() < 8)) {
					Utils.setToastCenter(WifiConfigerActivity.this, R.string.toast_wifi_pwd_length_err);
					return;
				}
				JSONObject json = new JSONObject();
				try {
					json.put("password", pwd);
					json.put("ssid", ssid);
					Bitmap bitmap = CodeCreator.createQRCode(json.toString());
					showQrCode(bitmap);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (WriterException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void showQrCode(Bitmap bitmap) {
		
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.push_qr_code);
        
        TextView tv_title = (TextView)window.findViewById(R.id.tv_title);
        tv_title.setText(R.string.descrip_qr);
        
        ImageView iv_qrcode = (ImageView) window.findViewById(R.id.iv_qrcode);
        iv_qrcode.setImageBitmap(bitmap);
	}
}
