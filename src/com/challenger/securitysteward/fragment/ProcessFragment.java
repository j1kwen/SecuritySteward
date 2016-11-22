package com.challenger.securitysteward.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.adapter.FragmentViewPagerAdapter;
import com.challenger.securitysteward.adapter.FragmentViewPagerAdapter.OnExtraPageChangeListener;
import com.challenger.securitysteward.controls.TabButton;

public class ProcessFragment extends BaseFragment {

	private ViewPager mViewPager;
	private List<Fragment> mFragmentList;
	private List<TabButton> mTabButtonList = new ArrayList<TabButton>();
    private int[] mTabButtonIds = new int[] {
    							R.id.tab_first,
    							R.id.tab_second};
	
	public ProcessFragment(String title) {
		super(title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mCurrentView = inflater.inflate(R.layout.view_process, container, false);
		
		mViewPager = (ViewPager) mCurrentView.findViewById(R.id.viewpager_process);
		for(int id : mTabButtonIds) {			
			mTabButtonList.add((TabButton)mCurrentView.findViewById(id));
		}
		mTabButtonList.get(0).setAlpha(1.0f);
		
		mFragmentList = new ArrayList<Fragment>();
		mFragmentList.add(new DownloadFragment(getString(R.string.tab_item_downloading)));
		mFragmentList.add(new FinishedFragment(getString(R.string.tab_item_finished)));
		
		FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getFragmentManager(), mViewPager, mFragmentList);
		adapter.setOnExtraPageChangeListener(new OnExtraPageChangeListener() {
			
			@Override
			public void onExtraPageSelected(int position) {
				
			}
			
			@Override
			public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (positionOffsetPixels != 0){
		            mTabButtonList.get(position).setAlpha(1 - positionOffset);
		            mTabButtonList.get(position+1).setAlpha(positionOffset);
		        }
			}
			
			@Override
			public void onExtraPageScrollStateChanged(int state) {
				
			}
		});
		
		for(int i = 0; i < mTabButtonList.size(); i++){
            mTabButtonList.get(i).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int cur = (Integer) v.getTag();
					changeAlpha(cur);
			        mViewPager.setCurrentItem(cur, false);
				}
			});
            mTabButtonList.get(i).setTag(i);
           
            mTabButtonList.get(i).setMessageNumber(0);
        }
		
		return mCurrentView;
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
		
	}
}
