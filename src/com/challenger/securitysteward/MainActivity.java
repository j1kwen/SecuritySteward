package com.challenger.securitysteward;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.challenger.securitysteward.adapter.FragmentViewPagerAdapter;
import com.challenger.securitysteward.adapter.FragmentViewPagerAdapter.OnExtraPageChangeListener;
import com.challenger.securitysteward.controls.ActionItem;
import com.challenger.securitysteward.controls.TabButton;
import com.challenger.securitysteward.controls.TitlePopup;
import com.challenger.securitysteward.controls.TitlePopup.OnItemOnClickListener;
import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.controls.UnScrollViewPage;
import com.challenger.securitysteward.fragment.DevicesFragment;
import com.challenger.securitysteward.fragment.ProcessFragment;
import com.challenger.securitysteward.fragment.UserFragment;
import com.challenger.securitysteward.model.DeviceMessage;
import com.challenger.securitysteward.utils.Utils;
import com.karics.library.zxing.android.CaptureActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnExtraPageChangeListener {
   
	private static final String TAG = "MainActivity";
	private boolean mSupport = true;
	private TopBar mTopBar;
	private TitlePopup mTitlePopupAdd;
	private UnScrollViewPage mViewPager;
    private List<Fragment> mFragmentList;
    private List<TabButton> mTabButtonList = new ArrayList<TabButton>();
    private int[] mTabButtonIds = new int[] {
    							R.id.tab_first,
    							R.id.tab_second,
    							R.id.tab_third};
    
    public static final int REQUEST_BIND		= 0x0456;
    
	@SuppressLint("InlinedApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "Main activity");
        
        setContentView(R.layout.activity_main);
        
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		@SuppressWarnings("deprecation")
		String mCPU_ABI = Build.CPU_ABI;

		if(mCPU_ABI.contains("86")) {

			mSupport = false;
		} else {
			Utils.initSDKServices(getApplicationContext());
			Log.d(TAG, " sdk init has finished");
//			Intent intent = new Intent(this, PushService.class);
//	        startService(intent);
			mSupport = true;
		}
		initViews();
		setPagerAdapter();
		initEventListener();
        
        if(!mSupport) {
        	
        	new AlertDialog.Builder(MainActivity.this)
				.setTitle("Unsupport Device")
				.setMessage(getResources().getString(R.string.device_unsupported)
							+ "\n\n(ERROR : CPU_ABI - " + mCPU_ABI + ")")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						System.exit(0);
					}
				})
				.setCancelable(false)
				.create().show();
        }
        Bundle bundle = getIntent().getBundleExtra(Utils.EXTRA_BUNDLE);
        if(bundle != null){
        	Log.d(TAG, "extra bundle exist");
        	Intent intent = new Intent(MainActivity.this, DeviceDetailsActivity.class);
        	DeviceMessage msg = (DeviceMessage) getIntent().getSerializableExtra("message");
        	String did = getIntent().getStringExtra("did");
        	Utils.getMessageManagement().addDeviceMessage(did, msg);
        	
			intent.putExtra("did", did);
			
			getIntent().removeExtra(Utils.EXTRA_BUNDLE);
			
			startActivity(intent);
        }
    }
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "require a new intent");
		Bundle bundle = intent.getBundleExtra(Utils.EXTRA_BUNDLE);
		if(bundle != null){
			Log.d(TAG, "extra bundle exist");
			Intent intent2 = new Intent(MainActivity.this, DeviceDetailsActivity.class);
			DeviceMessage msg = (DeviceMessage) intent.getSerializableExtra("message");
			String did = intent.getStringExtra("did");
			Utils.getMessageManagement().addDeviceMessage(did, msg);
			
			intent2.putExtra("did", did);
			
			intent.removeExtra(Utils.EXTRA_BUNDLE);
			
			changeAlpha(0);
			mViewPager.setCurrentItem(0, false);
			
			startActivity(intent2);
		}
	}

    /** 
     * Initialization, access controls reference
     */
	private void initViews() {
    	
        mViewPager = (UnScrollViewPage)findViewById(R.id.viewpager_main);
        mTopBar = (TopBar)findViewById(R.id.topbar_main);
        mTitlePopupAdd = new TitlePopup(MainActivity.this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //Add tabs to list
        for(int id : mTabButtonIds)
        	mTabButtonList.add((TabButton)findViewById(id));
        
        mTabButtonList.get(0).setAlpha(1.0f);
        mTopBar.setTitleText(mTabButtonList.get(0).getText());

    }

    /**
     * Set pages adapter
     */
    private void setPagerAdapter() {
    	
        mFragmentList = new ArrayList<Fragment>();
        
        mFragmentList.add(new DevicesFragment(mTabButtonList.get(0).getText()));
        mFragmentList.add(new ProcessFragment(mTabButtonList.get(1).getText()));
        mFragmentList.add(new UserFragment(mTabButtonList.get(2).getText()));
        
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, mFragmentList);
        adapter.setOnExtraPageChangeListener(this);
        
        mTitlePopupAdd.addAction(new ActionItem(this, R.string.popup_item_add_device, R.drawable.title_btn_function));
        mTitlePopupAdd.addAction(new ActionItem(this, R.string.popup_item_scan_qr, R.drawable.mm_title_btn_qrcode_normal));
        mTitlePopupAdd.addAction(new ActionItem(this, R.string.popup_item_set_wifi, R.drawable.mm_title_btn_wifi));
        
        
    }

    /**
     * Binding event listeners
     */
    private void initEventListener() {
    	
        for(int i = 0; i < mTabButtonList.size(); i++){
            mTabButtonList.get(i).setOnClickListener(this);
            mTabButtonList.get(i).setTag(i);
           
            mTabButtonList.get(i).setMessageNumber(0);
        }
        
        mTitlePopupAdd.setItemOnClickListener(new OnItemOnClickListener() {
			
			@Override
			public void onItemClick(ActionItem item, int position) {
				switch (position) {
				case 0:
					Intent intent0 = new Intent(MainActivity.this, BindDeviceActivity.class);
					startActivityForResult(intent0, REQUEST_BIND);
					break;
				case 1:
					Intent intent = new Intent(MainActivity.this,
							CaptureActivity.class);
					startActivityForResult(intent, Utils.REQUEST_CODE_SCAN);
					break;
				case 2:
					Intent intent2 = new Intent(MainActivity.this, WifiConfigerActivity.class);
					startActivity(intent2);
					break;
				default:
					break;
				}
			}
		});
        
        mTopBar.setOnClickListener(new TopBarClickListener() {
        	
        	@Override
        	public void onLeftClick(View v) {
        		
        		// No button
        	}
			
			@Override
			public void onRightClick(View v) {
				
				mTitlePopupAdd.show(v);
			}
		});
    } 
    @Override
    public void onClick(View v) {

        int number = (Integer) v.getTag();
        changeAlpha(number);
        
        mViewPager.setCurrentItem(number,false);
    }

    @Override
    public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    	
        if (positionOffsetPixels != 0){
            mTabButtonList.get(position).setAlpha(1 - positionOffset);
            mTabButtonList.get(position+1).setAlpha(positionOffset);
        }
    }

	@Override
	public void onExtraPageSelected(int position) {
		
		mTopBar.setTitleText(mTabButtonList.get(position).getText());
	}

	@Override
	public void onExtraPageScrollStateChanged(int state) {
		
	}
	
	public void changeAlpha(int number){
		
		for (TabButton btn : mTabButtonList){
			btn.setAlpha(0f);
		}
		mTabButtonList.get(number).setAlpha(1.0f);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	super.onActivityResult(requestCode, resultCode, data);
    			
		if (requestCode == Utils.REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				
				String content = data.getStringExtra(Utils.DECODED_CONTENT_KEY);
				@SuppressWarnings("unused")
				Bitmap bitmap = data.getParcelableExtra(Utils.DECODED_BITMAP_KEY);
				try {
					JSONObject json = new JSONObject(content);
					if(json.getInt("action") == 0 && json.getString("did") != null) {
						Intent intent = Utils.setIntentExtras(new Intent(MainActivity.this, BindDeviceActivity.class),
								Utils.KEY_QRCODE_STRING, content);
						startActivityForResult(intent, REQUEST_BIND);
					} else {
						showQRResult(content);
					}
				} catch (JSONException e) {
					showQRResult(content);
				}
			}
		} else if(requestCode == REQUEST_BIND && resultCode == RESULT_OK) {
			changeAlpha(0);
			mViewPager.setCurrentItem(0, false);
		}
    }
	private void showQRResult(String text) {
		new AlertDialog.Builder(MainActivity.this)
		.setTitle("Result")
		.setMessage(text)
		.setPositiveButton("OK", null)
		.setCancelable(false)
		.create().show();
	}
	public void saveUserInfo() {
		saveObject(Utils.KEY_SHARED_USER_INFO, Utils.KEY_SHARED_USER_BOJECT, Utils.getUserManagement());
	}
	public void saveObject(String shared, String key, Object value) {
		
		SharedPreferences preferences = getSharedPreferences(shared,
				MODE_PRIVATE);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {

			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(value);

			String productBase64 = new String(Base64.encodeBase64(baos
					.toByteArray()));

			Editor editor = preferences.edit();
			editor.putString(key, productBase64);
			editor.commit();
			Log.d("Save cache","cache has saved");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void refreshMark() {
		int cnt = Utils.getMessageManagement().getMessageUnreadCount();
		mTabButtonList.get(0).setMessageNumber(cnt);
	}
	
}