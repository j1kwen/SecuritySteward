package com.challenger.securitysteward.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.challenger.securitysteward.R;

public class DownloadFragment extends BaseFragment {
	
	private ListView mListView = null;
	private TextView mTextViewNoItem = null;

	public DownloadFragment(String title) {
		super(title);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mCurrentView = inflater.inflate(R.layout.fragment_download,container, false);
		
		mListView = (ListView) mCurrentView.findViewById(R.id.lv_download);
		mTextViewNoItem = (TextView) mCurrentView.findViewById(R.id.tv_no_item);
		
		if(mListView.getCount() == 0) {
			mTextViewNoItem.setVisibility(View.VISIBLE);
		} else {
			mTextViewNoItem.setVisibility(View.GONE);
		}
		
		return mCurrentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mListView.getCount() == 0) {
			mTextViewNoItem.setVisibility(View.VISIBLE);
		} else {
			mTextViewNoItem.setVisibility(View.GONE);
		}
	}
}
