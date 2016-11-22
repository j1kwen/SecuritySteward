package com.challenger.securitysteward.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.challenger.securitysteward.MainActivity;
import com.challenger.securitysteward.R;
import com.challenger.securitysteward.adapter.DeviceItemAdapter;
import com.challenger.securitysteward.model.DeviceItemModel;
import com.challenger.securitysteward.model.DeviceMessage;
import com.challenger.securitysteward.model.MediaManagement.OnReloadImage;
import com.challenger.securitysteward.receivers.DeviceMessageReceiver;
import com.challenger.securitysteward.receivers.DeviceMessageReceiver.MessageListener;
import com.challenger.securitysteward.utils.SDKUtils;
import com.challenger.securitysteward.utils.SDKUtils.OnReceivedBytes;
import com.challenger.securitysteward.utils.SDKUtils.OnReceivedResult;
import com.challenger.securitysteward.utils.Utils;

public class DevicesFragment extends BaseFragment implements OnReceivedResult, OnReceivedBytes, OnReloadImage {
	
	@SuppressWarnings("unused")
	private static final String TAG = "DevicesFragment";
	private ListView mListDevices = null;
	private List<DeviceItemModel> mListObj = null;
	private DeviceItemAdapter mAdapter = null;
	private TextView mTextViewNoItem = null;
	private DeviceMessageReceiver receiver = null;

	public DevicesFragment(String title) {
		super(title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mCurrentView = inflater.inflate(R.layout.view_devices,container,false);
		//First tab - Devices
		
		mListDevices = (ListView) mCurrentView.findViewById(R.id.list_device);
		mTextViewNoItem = (TextView) mCurrentView.findViewById(R.id.tv_no_item);
		
		mListObj = Utils.getDeviceManagement().getDevicesList();
		
		mAdapter = new DeviceItemAdapter(getContext(), mListObj);
		
		mListDevices.setAdapter(mAdapter);
		
		receiver = new DeviceMessageReceiver();
		receiver.setMessageListener(new MessageListener() {
			
			@Override
			public void onReceive(String message) {
				Log.d("Devices", "received message => " + message);
				try {
					JSONObject json = new JSONObject(message);
					if(!json.has("title")) {
						// System message
						long date = json.getLong("timestamp");
						String body = json.getString("body");
						String did = json.getString("did");
						if(body.contains("@string:bind_success")) {
							body = getString(R.string.sys_bind_success);
						}
						if(body.contains("@string:modify_success")) {
							body = getString(R.string.sys_modify_success);
						}
						Utils.getMessageManagement().addDeviceMessage(did,
								new DeviceMessage(body, date));
						
						Utils.getMediaManagement().playSystemMessage();
						Utils.getDeviceManagement().resetOrder();
						mAdapter.notifyDataSetChanged();
						((MainActivity)getActivity()).refreshMark();
						
						Intent intentSend = new Intent();
						intentSend.setAction("com.challenger.securitysteward.SEND_DEVICE_MESSAGE_IN_ACTIVITY");
						intentSend.putExtra("data", message);
						((MainActivity)getActivity()).sendBroadcast(intentSend);
						return;
					}
					String title = json.getString("title");
					long date = json.getLong("timestamp");
					String body = json.getString("body");
					String extra = "";
					if(json.has("extra")) {
						extra = json.getString("extra");
					}
					String href = json.getString("href");
					String did = json.getString("did");
					Utils.getMessageManagement().addDeviceMessage(did,
															new DeviceMessage(title, date, body, extra, href, 1L));
					Utils.getMediaManagement().playReceived();
					
					Utils.getDeviceManagement().resetOrder();
					mAdapter.notifyDataSetChanged();
					Log.d("DEBUG", "adapter has been updated");
					((MainActivity)getActivity()).refreshMark();
					
					Intent intentSend = new Intent();
					intentSend.setAction("com.challenger.securitysteward.SEND_DEVICE_MESSAGE_IN_ACTIVITY");
					intentSend.putExtra("data", message);
					((MainActivity)getActivity()).sendBroadcast(intentSend);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		IntentFilter filter=new IntentFilter();
		filter.addAction("com.challenger.securitysteward.SEND_DEVICE_MESSAGE");
		this.getActivity().registerReceiver(receiver, filter);
		
		return mCurrentView;
	}
	
	@Override
	public void onDestroy() {
		this.getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d("onresume", "device fragment resumed");
		SDKUtils.getInstance().getDevicesList(Utils.SECRET_KEY, Utils.clientId, this);
		
		if(mListDevices.getCount() == 0) {
			mTextViewNoItem.setVisibility(View.VISIBLE);
		} else {
			mTextViewNoItem.setVisibility(View.GONE);
		}
		Utils.getDeviceManagement().resetOrder();
		mAdapter.notifyDataSetChanged(); 
		
		((MainActivity)getActivity()).refreshMark();
	}

	@Override
	public void onReceived(String result) {
		String val = result;
		ArrayList<DeviceItemModel> ret = new ArrayList<DeviceItemModel>();
		Log.d("received", val);
		try {
			JSONObject json = new JSONObject(val);
			if(json.getInt("result") == 0) {
				JSONArray list = json.getJSONArray("list");
				int cnt = list.length();
				for(int i = 0 ; i < cnt ; i++) {
					JSONObject obji = list.getJSONObject(i);
					String did = obji.getString("did");
					String name = obji.getString("name");
					String remark = obji.getString("remark");
					long last_modify = obji.getLong("last_modify");
					String image = obji.getString("image");
					long level = obji.getLong("level");
					
					checkIfDownloadImage(did, image);
					
					DeviceItemModel dev = new DeviceItemModel(did, name, remark, level, last_modify);
					ret.add(dev);
				}
				Utils.getDeviceManagement().setAllDevices(ret);
				Utils.getDeviceManagement().resetOrder();
				mAdapter.notifyDataSetChanged();
				if(mListDevices.getCount() == 0) {
					mTextViewNoItem.setVisibility(View.VISIBLE);
				} else {
					mTextViewNoItem.setVisibility(View.GONE);
				}
				Log.d("Handler", "Device list set" + ret == null ? "fail" : "success");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void checkIfDownloadImage(String did, String image) {
		if(image == null || image.equals("")) {
			return;
		}
				
		String md5 = Utils.getMediaManagement().getImageMd5(did);
		
		if(!md5.equals(image)) {
			SDKUtils.getInstance().getDeviceImage(Utils.SECRET_KEY, Utils.clientId, did, this);
			return;
		}
	}

	@Override
	public void onReceivedByte(String did, byte[] bytes) {
		Utils.getMediaManagement().writeImageToLocal(did, bytes);
		Utils.getMediaManagement().reloadLocalImage(this);
	}

	@Override
	public void onReload() {
		mAdapter.notifyDataSetChanged();
	}
}
