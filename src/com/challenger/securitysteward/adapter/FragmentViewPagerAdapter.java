package com.challenger.securitysteward.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;

public class FragmentViewPagerAdapter extends PagerAdapter implements OnPageChangeListener {

	private List<Fragment> mFragmentList;
    private FragmentManager mFragmentManager;
    private ViewPager mViewPager;
    private int mCurrentPageIndex = 0;

    private OnExtraPageChangeListener mOnExtraPageChangeListener;

	public FragmentViewPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager , List<Fragment> fragments) {
        
		this.mFragmentList = fragments;
        this.mFragmentManager = fragmentManager;
        this.mViewPager = viewPager;
        this.mViewPager.setAdapter(this);
        this.mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mFragmentList.get(position).getView());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = mFragmentList.get(position);
        if(!fragment.isAdded()) {
        	
            FragmentTransaction fragtran = mFragmentManager.beginTransaction();
            fragtran.add(fragment, fragment.getClass().getSimpleName());
            fragtran.commit();

            mFragmentManager.executePendingTransactions();
        }
        try {
        	
        	if(fragment.getView().getParent() == null) {
        		
        		container.addView(fragment.getView()); // add a layout to view pager
        	}
        } catch (NullPointerException e) {
        	e.printStackTrace();
        }

        return fragment.getView();
    }

    /**
     * Current view index
     * @return
     */
    public int getCurrentPageIndex() {
        return mCurrentPageIndex;
    }

    public OnExtraPageChangeListener getOnExtraPageChangeListener() {
        return mOnExtraPageChangeListener;
    }

    /**
     * Set extra listener
     * @param onExtraPageChangeListener
     */
    public void setOnExtraPageChangeListener(OnExtraPageChangeListener onExtraPageChangeListener) {
        this.mOnExtraPageChangeListener = onExtraPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(null != mOnExtraPageChangeListener) { // if extra interface exist
            mOnExtraPageChangeListener.onExtraPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
    	
        mFragmentList.get(mCurrentPageIndex).onPause();
        
        if(mFragmentList.get(position).isAdded()) {
            mFragmentList.get(position).onResume();
        }
        mCurrentPageIndex = position;

        if(null != mOnExtraPageChangeListener) { // if extra interface exist
            mOnExtraPageChangeListener.onExtraPageSelected(position);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    	
        if(null != mOnExtraPageChangeListener) { // if extra interface exist
            mOnExtraPageChangeListener.onExtraPageScrollStateChanged(state);
        }
    }


    /**
     * page extra listener
     */
    public interface OnExtraPageChangeListener {
    	
        public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        public void onExtraPageSelected(int position);
        public void onExtraPageScrollStateChanged(int state);
    }
}
