package com.challenger.securitysteward.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.WebBrowserActivity;
import com.challenger.securitysteward.model.DeviceMessage;
import com.challenger.securitysteward.utils.Utils;

public class DeviceMessageAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<DeviceMessage> mListMessage = null;
	
	public DeviceMessageAdapter(Context mContext, List<DeviceMessage> mListMessage) {
		super();
		this.mContext = mContext;
		this.mListMessage = mListMessage;
	}

	@Override
	public int getCount() {
		return mListMessage.size();
	}

	@Override
	public Object getItem(int position) {
		return mListMessage.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DeviceMessage dev = mListMessage.get(position);
		if(dev.isSystemMsg()) {
			
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_system_message, null);
			((TextView) convertView.findViewById(R.id.tv_msg_body)).setText(dev.getBody());
			
		} else {
			
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_message, null);
			if(dev.getTitle() != null) {
				((TextView) convertView.findViewById(R.id.tv_message_title)).setText(dev.getTitle());
			}
			((TextView) convertView.findViewById(R.id.tv_message_date)).setText(Utils.getDateFromUnix(dev.getDate()));
			if(dev.getBody() != null) {
				((TextView) convertView.findViewById(R.id.tv_message_body)).setText(dev.getBody());
			}
			if(dev.getExtra() == null || dev.getExtra().equals("")) {
				((TextView) convertView.findViewById(R.id.tv_message_extra)).setVisibility(View.GONE);
			} else {
				((TextView) convertView.findViewById(R.id.tv_message_extra)).setVisibility(View.VISIBLE);
				((TextView) convertView.findViewById(R.id.tv_message_extra)).setText(dev.getExtra());
			}
			
			((LinearLayout) convertView.findViewById(R.id.ll_dev_message)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String href = dev.getHref();
					if(href != null && (href.startsWith("http://") || href.startsWith("https://"))) {
						Intent intent = new Intent(mContext, WebBrowserActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("url", href);
						bundle.putString("title", dev.getTitle());
						intent.putExtras(bundle);
						mContext.startActivity(intent);
					}
				}
			});
		}
		
		return convertView;
	}
}
