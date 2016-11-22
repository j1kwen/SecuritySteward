package com.challenger.securitysteward.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.challenger.securitysteward.DeviceDetailsActivity;
import com.challenger.securitysteward.R;
import com.challenger.securitysteward.model.DeviceItemModel;
import com.challenger.securitysteward.model.DeviceMessage;
import com.challenger.securitysteward.utils.Utils;

public class DeviceItemAdapter extends BaseAdapter {

	/**
	 * Device list adapter
	 */
	
	private Context mContext = null;
	private List<DeviceItemModel> mListDevice = null;
	
	public DeviceItemAdapter(Context mContext, List<DeviceItemModel> mListDevice) {
		super();
		this.mContext = mContext;
		this.mListDevice = mListDevice;
	}

	@Override
	public int getCount() {
		return mListDevice.size();
	}

	@Override
	public Object getItem(int position) {
		return mListDevice.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null) {
			// Load view
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_device, null);
			holder = new ViewHolder();
			holder.mImageViewPic = (ImageView) convertView.findViewById(R.id.iv_avatar);
			holder.mRelaInfo = (RelativeLayout) convertView.findViewById(R.id.re_dev_info);
			holder.mTextViewName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.mTextViewRecent = (TextView) convertView.findViewById(R.id.tv_recent);
			holder.mViewDiv = convertView.findViewById(R.id.div_list_device);
			holder.mTextViewLastTime = (TextView) convertView.findViewById(R.id.tv_last_time);
			holder.iv_Unread = (ImageView) convertView.findViewById(R.id.iv_unread);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// set controls in view
		final DeviceItemModel info = mListDevice.get(position);
		holder.mTextViewName.setText(info.getName());
		List<DeviceMessage> message = Utils.getMessageManagement().getDeviceMessages(info.getDid());
		if(message.size() > 0) {
			holder.mTextViewRecent.setText(message.get(message.size() - 1).getTitle());			
		} else {
			holder.mTextViewRecent.setText(mContext.getString(R.string.text_no_message));
		}
		String time = info.getLastmodifyString();
		if(time.equals("yestoday")) {
			time = mContext.getString(R.string.text_yestoday);
		}
		holder.mTextViewLastTime.setText(time);
		Bitmap image = Utils.getMediaManagement().getDeviceBitmap(info.getDid());
		holder.mImageViewPic.setImageBitmap(image);
		if(position == 0) {
			holder.mViewDiv.setVisibility(View.GONE);
		}
		holder.iv_Unread.setVisibility(info.isHasUnread() ? View.VISIBLE : View.GONE);
		holder.mRelaInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
				intent.putExtra("did", info.getDid());
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}

	static class ViewHolder {
		TextView mTextViewName, mTextViewRecent, mTextViewLastTime;
		RelativeLayout mRelaInfo;
		ImageView mImageViewPic, iv_Unread;
		View mViewDiv;
	}
}
