package com.challenger.securitysteward;

import java.util.List;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.challenger.securitysteward.adapter.DeviceMessageAdapter;
import com.challenger.securitysteward.controls.TopBar;
import com.challenger.securitysteward.controls.TopBar.TopBarClickListener;
import com.challenger.securitysteward.model.DeviceItemModel;
import com.challenger.securitysteward.model.DeviceMessage;
import com.challenger.securitysteward.receivers.MessageInActReceicer;
import com.challenger.securitysteward.receivers.MessageInActReceicer.MessageListener;
import com.challenger.securitysteward.utils.Utils;

public class DeviceDetailsActivity extends BaseActivity {

	private DeviceItemModel mDeviceInfo = null;
	public static final int DEVICE_MORE = 0x01; 
	private ListView mListView;
	private List<DeviceMessage> mListMessage = null;
	private DeviceMessageAdapter adapter = null;
	private MessageInActReceicer receiver = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_details);
		initView();
		initEventListener();
	}

	@Override
	protected void initView() {
		mTopBar = (TopBar) findViewById(R.id.topbar_device_detail);
		String did = (String) getIntent().getStringExtra("did");
		mDeviceInfo = Utils.getDeviceManagement().get(did);
		mTopBar.setTitleText(mDeviceInfo.getName());
		Utils.getMessageManagement().setDeviceMessageReaded(did);
		int cnt = Utils.getMessageManagement().getMessageUnreadCount();
		if(cnt > 0) {
			mTopBar.setLeftText(String.format("%s(%d)", getString(R.string.tab_button1), cnt));
		} else {
			mTopBar.setLeftText(getString(R.string.tab_button1));
		}
		mListView = (ListView) findViewById(R.id.lv_device_msg);
		
		mListMessage = Utils.getMessageManagement().getDeviceMessages(mDeviceInfo.getDid());
		
		adapter = new DeviceMessageAdapter(DeviceDetailsActivity.this, mListMessage);
		
		mListView.setAdapter(adapter);
		
		mListView.setSelection(mListView.getCount() - 1);
		//mListView.smoothScrollToPosition(mListView.getCount() - 1);
		receiver = new MessageInActReceicer();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.challenger.securitysteward.SEND_DEVICE_MESSAGE_IN_ACTIVITY");
		receiver.setMessageListener(new MessageListener() {
			
			@Override
			public void onReceive(String message) {
				Log.d("DevicesDetails", "received message => " + message);

				adapter.notifyDataSetChanged();
				mListView.smoothScrollToPosition(mListView.getCount() - 1);
				Utils.getMessageManagement().setDeviceMessageReaded(mDeviceInfo.getDid());
				int cnt = Utils.getMessageManagement().getMessageUnreadCount();
				if(cnt > 0) {
					mTopBar.setLeftText(String.format("%s(%d)", getString(R.string.tab_button1), cnt));
				}
			}
		});
		registerReceiver(receiver, filter);
	}

	@Override
	protected void initEventListener() {
		mTopBar.setOnClickListener(new TopBarClickListener() {
			@Override
			public void onRightClick(View v) {
				Intent intent = new Intent(DeviceDetailsActivity.this, DeviceMoreActivity.class);
				intent.putExtra("device", mDeviceInfo);
				startActivityForResult(intent, DEVICE_MORE);
			}
			
			@Override
			public void onLeftClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == DEVICE_MORE) {
			if(resultCode == RESULT_OK) {
				finish();
			} else if(resultCode == RESULT_CANCELED) {
				updataDeviceInfo();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	private void updataDeviceInfo() {
		String did = (String) getIntent().getStringExtra("did");
		mDeviceInfo = Utils.getDeviceManagement().get(did);
		mTopBar.setTitleText(mDeviceInfo.getName());
		Utils.getMessageManagement().setDeviceMessageReaded(did);
		int cnt = Utils.getMessageManagement().getMessageUnreadCount();
		if(cnt > 0) {
			mTopBar.setLeftText(String.format("%s(%d)", getString(R.string.tab_button1), cnt));
		} else {
			mTopBar.setLeftText(getString(R.string.tab_button1));
		}
		adapter.notifyDataSetChanged();
	}
}
